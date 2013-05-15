package org.livefx

import org.livefx.script.Message
import org.livefx.script.Change
import org.livefx.script.Location
import org.livefx.script.Start
import org.livefx.script.End
import org.livefx.script.NoLo
import org.livefx.script.Remove
import org.livefx.script.Include
import org.livefx.script.Update
import org.livefx.script.Reset
import org.livefx.script.Script

trait LiveLiveSeq[A] {
  def observable: OldLiveSeq[A]

  def map[B](f: A => B): LiveLiveSeq[B] = observable.liveMap(f).live
  
  def flatMap[B](f: A => LiveLiveSeq[B]): LiveLiveSeq[B] = {
    val outer = this
    val binding = new ArrayBuffer[B] with LiveBuffer[B] {
      private final def target = this
      
//      private val ref = outer.observable.changes.subscribeWeak { (pub: OldLiveSeq[A], change: Change[A]) =>
//        def process(pub: OldLiveSeq[A], change: Change[A]): Unit = {
//          change match {
//            case Remove(location, oldElem) => location match {
//              case Start => target.remove(0)
//              case End => target.remove(target.size - 1)
//              case Index(index) => target.remove(index)
//              case NoLo => assert(false)
//            }
//            case Include(location, newElem) => location match {
//              case Start => target.prepend(f(newElem))
//              case End => target.append(f(newElem))
//              case Index(index) => target.insert(index, f(newElem))
//              case NoLo => assert(false)
//            }
//            case Update(location, newElem, oldElem) => location match {
//              case Start => target(0) = f(newElem)
//              case End => target(target.size - 1) = f(newElem)
//              case Index(index) => target(index) = f(newElem)
//              case NoLo => assert(false)
//            }
//            case Reset => target.clear()
//            case s: Script[A] => for (e <- s) process(pub, e)
//          }
//        }
//  
//        process(pub, change)
//      }
//      var nested: LiveValue[B] = f(source.value)
//      val nestedSpoilHandler = { (_: Any, spoilEvent: Spoil) => spoil(spoilEvent) }
//      var ref: Any = nested.spoils.subscribeWeak(nestedSpoilHandler)
//      val ref1 = source.spoils.subscribeWeak { (_, spoilEvent) =>
//        nested.spoils.unsubscribe(nestedSpoilHandler)
//        nested = f(source.value)
//        ref = nested.spoils.subscribeWeak(nestedSpoilHandler)
//        spoil(spoilEvent)
//      }
//      
//      protected override def computeValue: B = nested.value
    }
    binding.live
  }
}

trait OldLiveSeq[A] extends Seq[A] with Publisher {
  type Pub <: OldLiveSeq[A]

  protected lazy val changesSink = new EventSource[Pub, Change[A]](publisher)
  
  def changes: Events[Pub, Change[A]] = changesSink

  def apply(location: Location): Option[A] = location match {
    case Start(index) => Some(this(index))
    case End(index) => Some(this(this.size - index - 1))
    case NoLo => None
  }
  
  object live extends LiveLiveSeq[A] {
    override def observable = publisher
  }
  
  def liveMap[B](f: A => B): Seq[B] with OldLiveSeq[B] = {
    val outer = this
    new ArrayBuffer[B] with LiveBuffer[B] {
      private final def target = this
      private val ref = outer.changes.subscribeWeak { (pub: OldLiveSeq[A], change: Change[A]) =>
        def process(pub: OldLiveSeq[A], change: Change[A]): Unit = {
          change match {
            case Remove(location, oldElem) => location match {
              case Start(index) => target.remove(index)
              case End(index) => target.remove(target.size - index - 1)
              case NoLo => assert(false)
            }
            case Include(location, newElem) => location match {
              case Start(index) => target.insert(index, f(newElem))
              case End(0) => target.append(f(newElem))
              case NoLo => assert(false)
            }
            case Update(location, newElem, oldElem) => location match {
              case Start(index) => target(index) = f(newElem)
              case End(0) => target(target.size - 1) = f(newElem)
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
      val result2 = outer.changes.subscribeWeak { (pub: OldLiveSeq[A], change: Change[A]) =>
        def process(pub: OldLiveSeq[A], change: Change[A]): Unit = {
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
