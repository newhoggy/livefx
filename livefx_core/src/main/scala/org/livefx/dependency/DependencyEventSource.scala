package org.livefx.dependency

import scala.collection.immutable.HashSet
import scala.concurrent._
import org.livefx.util.TidyReferenceQueue
import org.livefx.util.TidyWeakReference
import org.livefx.Disposable
import org.livefx.EventSink
import org.livefx.Events

class DependencyEventSource[E] extends Events[E] with EventSink[E] {
  protected var subscribers = HashSet.empty[E => Unit]

  override def subscribe(subscriber: E => Unit): Disposable = {
    TidyReferenceQueue.tidy(2)

    val ref = new TidyWeakReference[E => Unit](subscriber, TidyReferenceQueue) with (E => Unit) {
      override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = {
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
