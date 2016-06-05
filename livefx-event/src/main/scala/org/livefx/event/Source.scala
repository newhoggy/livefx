package org.livefx.event

import java.io.Closeable

import org.livefx.core.disposal.{Closed, Disposable}

trait Source[E] extends Closeable { self =>
  def subscribe(subscriber: E => Unit): Closeable
}

object Source {
  implicit def disposableEventSource_YYKh2cf[A] = new Disposable[Source[A]] {
    override protected def onDispose(a: Source[A]): Unit = {
      a.close()
    }
  }

  implicit def empty = new Source[Nothing] {
    override def subscribe(subscriber: Nothing => Unit): Closeable = Closed

    override def close(): Unit = ()
  }
}
