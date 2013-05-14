package org.livefx

import org.livefx.util.Memoize
import org.livefx.trees.indexed.Tree
import org.livefx.trees.indexed.Leaf
import org.livefx.script._

import scalaz.Monoid

trait LiveSeq[+A] extends Spoilable {
  type Pub <: LiveSeq[A]
  
  def changes: Events[Pub, Change[A]]
  
  def size: LiveValue[Int]
  
  def asLiveSeq: LiveSeq[A] = this
  
  def map[B](f: A => B): LiveSeq[B]

  def +[B >: A](that: LiveSeq[B]): LiveSeq[B] = {
    val self = this
    new LiveSeqBinding[B] {
      // TODO: Hook up events
      private lazy val _spoils = new EventSource[Pub, Spoil](publisher)
      
      protected override def spoilsSource: EventSink[Spoil] = _spoils
      
      override def spoils: Events[Pub, Spoil] = _spoils

      def size: LiveValue[Int] = for (selfSize <- self.size; thatSize <- that.size) yield selfSize + thatSize
      
      def map[C](f: B => C): LiveSeq[C] = self.map(f) + that.map(f)
    }
  }
}
