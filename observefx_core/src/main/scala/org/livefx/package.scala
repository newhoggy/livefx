package org

import scala.collection.{ mutable => mutable }
import org.livefx.script._

package object livefx {
  type ArrayBuffer[A] = mutable.ArrayBuffer[A]
  type Buffer[A] = mutable.Buffer[A]
  type HashMap[A, B] = mutable.HashMap[A, B]
  type HashSet[A] = mutable.HashSet[A]
  type Map[A, B] = mutable.Map[A, B]
  type Set[A] = mutable.Set[A]
  type Undoable = mutable.Undoable
  
  type LiveSeq[A] = Publisher[Message[A] with Undoable]

  implicit class RichLiveBuffer[A](val in: Buffer[A] with LiveBuffer[A]) {
    def asSeq: Seq[A] with LiveSeq[A] = in
  }
  
  implicit class RichLiveSeq[A](val in: Seq[A] with LiveSeq[A]) {
    def apply(location: Location): Option[A] = location match {
      case Start => Some(in(0))
      case End => Some(in(in.size - 1))
      case Index(index) => Some(in(index))
      case NoLo => None
    }

    def liveMap[B](f: A => B): Seq[B] with LiveSeq[B] = new ArrayBuffer[B] with LiveBuffer[B] {
      private final def target = this
      in.subscribe { (pub: in.Pub, message: Message[A] with Undoable) =>
        def process(pub: in.Pub, message: Message[A]): Unit = {
          message match {
            case Remove(location, oldElem) => location match {
              case Start => target.remove(0)
              case End => target.remove(target.size - 1)
              case Index(index) => target.remove(index)
              case NoLo => assert(false)
            }
            case Include(location, newElem) => location match {
              case Start => target.prepend(f(newElem))
              case End => target.append(f(newElem))
              case Index(index) => target.insert(index, f(newElem))
              case NoLo => assert(false)
            }
            case Update(location, newElem, oldElem) => location match {
              case Start => target(0) = f(newElem)
              case End => target(target.size - 1) = f(newElem)
              case Index(index) => target(index) = f(newElem)
              case NoLo => assert(false)
            }
            case x: Reset[A] => target.clear()
            case s: Script[A] => for (e <- s) process(pub, e)
          }
        }

        process(pub, message)
      }
    }
    
    def liveCounted: Map[A, Int] with LiveMap[A, Int] = new HashMap[A, Int] with LiveMap[A, Int] {
      private final def target = this
      def attach(key: A): Unit = target(key) = target.getOrElse(key, 0) + 1
      def release(key: A): Unit = {
        val oldCount = target.getOrElse(key, 0)
        if (oldCount <= 1) {
          target.remove(key)
        } else {
          target(key) = oldCount - 1
        }
      }
      in.subscribe { (pub: in.Pub, message: Message[A] with Undoable) =>
        def process(pub: in.Pub, message: Message[A]): Unit = {
          message match {
            case Remove(_, oldElem) => release(oldElem)
            case Include(_, newElem) => attach(newElem)
            case Update(location, newElem, oldElem) => release(oldElem); attach(newElem)
            case x: Reset[A] => target.clear()
            case s: Script[A] => for (e <- s) process(pub, e)
          }
        }

        process(pub, message)
      }
    }
    
    def liveHashed: Set[A] with LiveSet[A] = new HashSet[A] with LiveSet[A] {
      private final def target = this
      in.subscribe { (pub: in.Pub, message: Message[A] with Undoable) =>
        def process(pub: in.Pub, message: Message[A]): Unit = {
          message match {
            case r: Remove[A] => r.location match {
              case Start => target.remove(in(0))
              case End => target.remove(in(target.size - 1))
              case Index(index) => target.remove(in(index))
              case NoLo => assert(false)
            }
            case i: Include[A] => i.location match {
              case Start => target.add(i.elem)
              case End => target.add(i.elem)
              case Index(index) => target.add(i.elem)
              case NoLo => assert(false)
            }
            case u: Update[A] => u.location match {
              case Start => target.remove(in(0)); target.add(u.elem)
              case End => target.remove(in(target.size - 1)); target.add(u.elem)
              case Index(index) => target.remove(in(index)); target.add(u.elem)
              case NoLo => assert(false)
            }
            case x: Reset[A] => target.clear()
            case s: Script[A] => for (e <- s) process(pub, e)
          }
        }

        process(pub, message)
      }
    }
  }
}
