package org

import scala.collection.mutable._
import org.livefx.script._

package object livefx {
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
      in.subscribe(new in.Sub {
        def process(pub: in.Pub, message: Message[A]): Unit = {
          message match {
            case r: Remove[A] => r.location match {
              case Start => target.remove(0)
              case End => target.remove(target.size - 1)
              case Index(index) => target.remove(index)
              case NoLo => assert(false)
            }
            case i: Include[A] => i.location match {
              case Start => target.prepend(f(i.elem))
              case End => target.append(f(i.elem))
              case Index(index) => target.insert(index, f(i.elem))
              case NoLo => assert(false)
            }
            case u: Update[A] => u.location match {
              case Start => target(0) = f(u.elem)
              case End => target(target.size - 1) = f(u.elem)
              case Index(index) => target(index) = f(u.elem)
              case NoLo => assert(false)
            }
            case x: Reset[A] => target.clear()
            case s: Script[A] => for (e <- s) process(pub, e)
          }
        }

        override def notify(pub: in.Pub, message: Message[A] with Undoable): Unit = process(pub, message)
      })
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
      in.subscribe(new in.Sub {
        def process(pub: in.Pub, message: Message[A]): Unit = {
          message match {
            case r: Remove[A] => release(r.elem)
            case i: Include[A] => attach(i.elem)
            case u: Update[A] => in(u.location).map{ e => println("--> e: " + e); release(e); attach(u.elem); println("--> elem: " + u.elem) }
            case x: Reset[A] => target.clear()
            case s: Script[A] => for (e <- s) process(pub, e)
          }
        }

        override def notify(pub: in.Pub, message: Message[A] with Undoable): Unit = process(pub, message)
      })
    }
    
    def liveHashed: Set[A] with LiveSet[A] = new HashSet[A] with LiveSet[A] {
      private final def target = this
      in.subscribe(new in.Sub {
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

        override def notify(pub: in.Pub, message: Message[A] with Undoable): Unit = process(pub, message)
      })
    }
  }
}
