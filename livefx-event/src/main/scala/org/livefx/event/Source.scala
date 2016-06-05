package org.livefx.event

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.livefx.core.disposal.Closed
import org.livefx.core.std.autoCloseable._
import org.livefx.core.syntax.disposable._
import org.livefx.core.syntax.std.atomicReference._

trait Source[+A] extends Closeable { self =>
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

  /** From a function that maps each event into an iterable event, create a new Source that will
    * emit each element of the iterable event.
    */
  def mapConcat[B](f: A => Iterable[B]): Source[B] = {
    new SimpleBus[B] { temp =>
      val subscription = self.subscribe(f(_).foreach(temp.publish))
    }
  }

  /** Compose two sources of compatible type together into a new source that emits the same events
    * as either of the two originals.
    */
  def merge[B >: A](that: Source[B]): Source[B] = {
    new SimpleBus[B] { temp =>
      val subscription = self.subscribe(temp.publish) ++ that.subscribe(temp.publish)
    }
  }

  /** Fold the event source into a value given the value's initial state.
    *
    * @param f The folding function
    * @param initial The initial state
    * @tparam B Type of the new value
    * @return The value.
    */
  def foldRight[B](initial: B)(f: (A, => B) => B): Live[B] = {
    val state = new AtomicReference[B](initial)

    new SimpleBus[B] with Live[B] { temp =>
      override def value: B = state.get

      self.subscribe { e =>
        val (_, newValue) = state.update(v => f(e, v))
        temp.publish(newValue)
      }
    }
  }

  /** Direct all events into the sink.
    */
  def into(sink: Sink[A]): Closeable = this.subscribe(sink.publish)
}

object Source {
  /** A source that never emits any events.
    */
  val empty = new Source[Nothing] {
    override def subscribe(subscriber: Nothing => Unit): Closeable = Closed

    override def close(): Unit = ()
  }
}
