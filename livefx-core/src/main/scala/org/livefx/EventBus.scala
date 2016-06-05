package org.livefx

import org.livefx.disposal.Disposable

import scala.collection.immutable.HashSet
import org.livefx.util.TidyWeakReference
import org.livefx.util.TidyReferenceQueue

import scala.concurrent._
import ExecutionContext.Implicits.global

class EventBus[E] extends EventSource[E] with EventSink[E] {
  protected var subscribers = HashSet.empty[E => Unit]

  override def subscribe(subscriber: E => Unit): Disposable = {
    TidyReferenceQueue.tidy(2)

    val ref = new TidyWeakReference[E => Unit](subscriber, TidyReferenceQueue) with (E => Unit) {
      override def onDispose(): Unit = {
        subscribers -= this
        future {}
      }

      override def apply(event: E): Unit = this.get.map(s => s(event))
    }

    subscribers += ref
    ref
  }

  final def isEmpty: Boolean = subscribers.isEmpty

  def publish(event: E): Unit = {
    TidyReferenceQueue.tidy(1)
    subscribers.foreach(s => s(event))
  }
}
