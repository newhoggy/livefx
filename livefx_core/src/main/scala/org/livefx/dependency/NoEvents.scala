package org.livefx.dependency

import org.livefx.Disposable
import org.livefx.Disposed

object NoEvents extends Events[Nothing] with EventSink[Nothing] {
  override def subscribe(subscriber: Nothing => Unit): Disposable = Disposed
  override def publish(event: Nothing): Unit = Unit
}
