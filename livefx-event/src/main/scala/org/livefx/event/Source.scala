package org.livefx.event

import java.io.Closeable

import org.livefx.core.disposal.{Closed, Disposer}

trait Source[+A] extends Disposer { self =>
  /** Subscribe a subscriber to a source.  The subscriber will be invoked with any events that the
    * source may emit.
    */
  def subscribe(subscriber: A => Unit): Closeable

  /** Create a new Source that will emit transformed events that have been emitted by the original
    * Source.  The transformation is described by the function argument.
    */
  def map[B](f: A => B): Source[B] = {
    new SimpleSinkSource[A, B] { temp =>
      override def transform: A => B = f

      val subscription = self.subscribe(temp.publish)
    }
  }
}

object Source {
  /** A source that never emits any events.
    */
  val empty = new Source[Nothing] {
    override def subscribe(subscriber: Nothing => Unit): Closeable = Closed
  }
}
