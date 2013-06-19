package org.livefx

import scala.collection.immutable.HashSet
import org.livefx.util.TidyWeakReference
import org.livefx.util.TidyReferenceQueue
import scala.concurrent._

trait EventSource[E] extends Events[E] with EventSink[E] {
  protected var subscribers = HashSet.empty[E => Unit]

  override def subscribe(subscriber: E => Unit): Disposable = new Disposable {
    TidyReferenceQueue.tidy(1)

    subscribers += subscriber

    override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = future(subscribers -= subscriber)
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

  final def isEmpty: Boolean = subscribers.isEmpty
}
