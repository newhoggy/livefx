package org.livefx

import org.livefx.util.Memoize
import org.livefx.trees.indexed.Tree
import org.livefx.trees.indexed.Leaf
import org.livefx.script._
import scalaz.Monoid
import org.livefx.script.Change

trait LiveSeq[+A] extends Spoilable {
  type Pub <: LiveSeq[A]
  
  def changes: Events[Pub, Change[A]]
  
  def size: LiveValue[Int]
  
  def asLiveSeq: LiveSeq[A] = this
  
  def map[B](f: A => B): LiveSeq[B]

  def +[B >: A](bSeq: LiveSeq[B]): LiveSeq[B] = {
    val aSeq = this
    new LiveSeqBinding[B] {
      var aOldSize = aSeq.size.value
      var bOldSize = bSeq.size.value

      val aRef = aSeq.changes.subscribeWeak { (_, change) =>
        change match {
          case include@Include(location, newElem) => {
            val translatedLocation = location match {
              case start: Start => start
              case end: End => end.asStart(aOldSize)
              case NoLo => throw new IndexOutOfBoundsException
            }
            _changes.publish(include.copy(location = translatedLocation))
            aOldSize += 1
          }
          case update@Update(location, newElem, oldElem) => {
            val translatedLocation = location match {
              case start: Start => start
              case end: End => end.asStart(aOldSize)
              case NoLo => throw new IndexOutOfBoundsException
            }
            _changes.publish(update.copy(location = translatedLocation))
            // aOldSize no change
          }
          case remove@Remove(location, oldElem) => {
            val translatedLocation = location match {
              case start: Start => start
              case end: End => end.asStart(aOldSize)
              case NoLo => throw new IndexOutOfBoundsException
            }
            _changes.publish(remove.copy(location = translatedLocation))
            aOldSize -= 1
          }
        }
      }
      
      val bRef = bSeq.changes.subscribeWeak { (_, change) =>
        change match {
          case include@Include(location, newElem) => {
            val translatedLocation = location match {
              case start: Start => start.add(aOldSize)
              case end: End => end.asStart(bOldSize).add(aOldSize)
              case NoLo => throw new IndexOutOfBoundsException
            }
            _changes.publish(include.copy(location = translatedLocation))
            bOldSize += 1
          }
          case update@Update(location, newElem, oldElem) => {
            val translatedLocation = location match {
              case start: Start => start.add(aOldSize)
              case end: End => end.asStart(bOldSize).add(aOldSize)
              case NoLo => throw new IndexOutOfBoundsException
            }
            _changes.publish(update.copy(location = translatedLocation))
            // aOldSize no change
          }
          case remove@Remove(location, oldElem) => {
            val translatedLocation = location match {
              case start: Start => start.add(aOldSize)
              case end: End => end.asStart(bOldSize).add(aOldSize)
              case NoLo => throw new IndexOutOfBoundsException
            }
            _changes.publish(remove.copy(location = translatedLocation))
            aOldSize -= 1
          }
        }
      }
      
      
      // TODO: Hook up events
      private lazy val _spoils = new EventSource[Pub, Spoil](publisher)
      
      protected override def spoilsSource: EventSink[Spoil] = _spoils
      
      override def spoils: Events[Pub, Spoil] = _spoils

      def size: LiveValue[Int] = for (aSeqSize <- aSeq.size; thatSize <- bSeq.size) yield aSeqSize + thatSize
      
      def map[C](f: B => C): LiveSeq[C] = aSeq.map(f) + bSeq.map(f)
    }
  }
}
