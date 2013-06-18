package org.livefx

import scala.collection.immutable.HashSet
import org.livefx.util.TidyWeakReference
import org.livefx.util.TidyReferenceQueue

class EventSource[E] extends Events[E] with EventSink[E] {
  private var subscribers = HashSet.empty[E => Unit]

  override def subscribe(subscriber: E => Unit): Disposable = new Disposable {
    TidyReferenceQueue.tidy(1)

    subscribers += subscriber

    override def dispose(): Unit = subscribers -= subscriber
  }

  override def subscribeWeak(subscriber: E => Unit): Disposable = {
    TidyReferenceQueue.tidy(2)

    val ref = new TidyWeakReference[E => Unit](subscriber, TidyReferenceQueue) with (E => Unit) {
      override def dispose(): Unit = subscribers -= this

      override def apply(event: E): Unit = this.get.map(s => s(event))
    }

    subscribers += ref
    ref
  }

  override def unsubscribe(subscriber: E => Unit): Unit = { subscribers -= subscriber }

  override def publish(event: E): Unit = {
    subscribers.foreach { entry => entry(event) }
  }

  final def isEmpty: Boolean = subscribers.isEmpty
}
