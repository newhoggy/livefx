package org.livefx.event

import java.io.Closeable

import org.livefx.value.util.{TidyReferenceQueue, TidyWeakReference}

import scala.collection.immutable.HashSet
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class EventBus[E] extends EventSource[E] with EventSink[E] {
  protected var subscribers = HashSet.empty[E => Unit]

  override def subscribe(subscriber: E => Unit): Closeable = {
    TidyReferenceQueue.tidy(2)

    val ref = new TidyWeakReference[E => Unit](subscriber, TidyReferenceQueue) with (E => Unit) {
      override def close(): Unit = {
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

  override def close(): Unit = ???
}
