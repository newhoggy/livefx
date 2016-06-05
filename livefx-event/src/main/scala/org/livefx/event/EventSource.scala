package org.livefx.event

import java.io.Closeable

import org.livefx.core.disposal.Disposable
import org.livefx.core.std.autoCloseable._
import org.livefx.core.syntax.disposable._

trait EventSource[+E] extends Closeable { self =>
  def subscribe(subscriber: E => Unit): Closeable
  def asEvents: EventSource[E] = this

}

object EventSource {
  implicit def disposableEventSource_YYKh2cf[A] = new Disposable[EventSource[A]] {
    override protected def onDispose(a: EventSource[A]): Unit = {
      a.close()
    }
  }
}
