package org.livefx.event.syntax

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.livefx.core.std.autoCloseable._
import org.livefx.core.syntax.disposable._
import org.livefx.core.syntax.std.atomicReference._
import org.livefx.event._

package object source {
  implicit class SourceOps_qMVKwgW[A](val self: Source[A]) extends AnyVal {
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
    def into(sink: Sink[A]): Closeable = self.subscribe(sink.publish)
  }

  implicit class SourceOps_KhVNHpu[A, B](val self: Source[Either[A, B]]) extends AnyVal {
    def divertLeft(sink: Sink[A]): Source[B] = {
      new SimpleBus[B] {
        temp =>
        val subscription = self.subscribe {
          case Right(rt) => temp.publish(rt)
          case Left(lt) => sink.publish(lt)
        }
      }
    }

    def divertRight(sink: Sink[B]): Source[A] = {
      new SimpleBus[A] {
        temp =>
        val subscription = self.subscribe {
          case Right(rt) => sink.publish(rt)
          case Left(lt) => temp.publish(lt)
        }
      }
    }
  }
}
