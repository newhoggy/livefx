package org.livefx

import org.livefx.script.Message
import org.livefx.script.Change
import org.livefx.script.Location
import org.livefx.script.Start
import org.livefx.script.End
import org.livefx.script.Index
import org.livefx.script.NoLo
import org.livefx.script.Remove
import org.livefx.script.Include
import org.livefx.script.Update
import org.livefx.script.Reset
import org.livefx.script.Script

trait LiveSeq[A] extends Seq[A] with Changeable[A, Change[A]] {
  type Pub <: LiveSeq[A]

  def apply(location: Location): Option[A] = location match {
    case Start => Some(this(0))
    case End => Some(this(this.size - 1))
    case Index(index) => Some(this(index))
    case NoLo => None
  }
  
  def liveMap[B](f: A => B): Seq[B] with LiveSeq[B] = {
    val outer = this
    new ArrayBuffer[B] with LiveBuffer[B] {
      private final def target = this
      private val ref = outer.changes.subscribeWeak { (pub: LiveSeq[A], change: Change[A]) =>
        def process(pub: LiveSeq[A], change: Change[A]): Unit = {
          change match {
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
            case Reset => target.clear()
            case s: Script[A] => for (e <- s) process(pub, e)
          }
        }
  
        process(pub, change)
      }
    }
  }
  
  def liveCounted: Map[A, Int] with LiveMap[A, Int] = {
    val outer = this
    new HashMap[A, Int] with LiveMap[A, Int] {
      private final def target = this
      def attach(key: A): Unit = {
        val newCount = target.getOrElse(key, 0) + 1
        target(key) = newCount 
      }
      def release(key: A): Unit = {
        val oldCount = target.getOrElse(key, 0)
        if (oldCount <= 1) {
          target -= key
        } else {
          target += (key -> (oldCount - 1))
        }
      }
      val result2 = outer.changes.subscribeWeak { (pub: LiveSeq[A], change: Change[A]) =>
        def process(pub: LiveSeq[A], change: Change[A]): Unit = {
          change match {
            case Remove(_, oldElem) => release(oldElem)
            case Include(_, newElem) => attach(newElem)
            case Update(location, newElem, oldElem) => release(oldElem); attach(newElem)
            case Reset => target.clear()
            case s: Script[A] => for (e <- s) process(pub, e)
          }
        }
  
        process(pub, change)
      }
    }
  }
  
  def liveSet: Set[A] with LiveSet[A] = liveCounted.liveKeys
}
