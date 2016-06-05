package org.livefx.event

import java.io.Closeable

import org.livefx.core.disposal.{Closed, Disposable}
import org.livefx.core.std.autoCloseable._
import org.livefx.core.syntax.disposable._

trait Source[+A] extends Closeable { self =>
  def subscribe(subscriber: A => Unit): Closeable

  def map[B](f: A => B): Source[B] = {
    new SimpleSinkSource[A, B] { temp =>
      override def transform: A => B = f

      val subscription = self.subscribe(temp.publish)
    }
  }

  /** Compose two sources of compatible type together into a new source that emits the same events
    * as the original.
    */
  def |[B >: A](that: Source[B]): Source[B] = {
    new SimpleSinkSource[B, B] { temp =>
      override def transform: B => B = identity

      val subscription = self.subscribe(temp.publish) ++ that.subscribe(temp.publish)
    }
  }
}

object Source {
  implicit def disposableEventSource_YYKh2cf[A] = new Disposable[Source[A]] {
    override protected def onDispose(a: Source[A]): Unit = {
      a.close()
    }
  }

  val empty = new Source[Nothing] {
    override def subscribe(subscriber: Nothing => Unit): Closeable = Closed

    override def close(): Unit = ()
  }
}
