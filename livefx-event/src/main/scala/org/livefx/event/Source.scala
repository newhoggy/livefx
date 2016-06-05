package org.livefx.event

import java.io.Closeable

import org.livefx.core.disposal.Disposable

trait Source[+E] extends Closeable { self =>
  def subscribe(subscriber: E => Unit): Closeable
}

object Source {
  implicit def disposableEventSource_YYKh2cf[A] = new Disposable[Source[A]] {
    override protected def onDispose(a: Source[A]): Unit = {
      a.close()
    }
  }
}
