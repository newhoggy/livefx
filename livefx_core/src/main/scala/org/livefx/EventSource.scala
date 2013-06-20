package org.livefx

import scala.collection.immutable.HashSet
import org.livefx.util.TidyWeakReference
import org.livefx.util.TidyReferenceQueue
import scala.concurrent._
import ExecutionContext.Implicits.global

trait EventSource[E] extends Events[E] with EventSink[E] {
  protected var subscribers = HashSet.empty[E => Unit]

  override def subscribeWeak(subscriber: E => Unit): Disposable = {
    TidyReferenceQueue.tidy(2)

    val ref = new TidyWeakReference[E => Unit](subscriber, TidyReferenceQueue) with (E => Unit) {
      override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = {
        println("dispose")
        subscribers -= this
        future {}
      }

      override def apply(event: E): Unit = this.get.map(s => s(event))
    }

    subscribers += ref
    ref
  }

  final def isEmpty: Boolean = subscribers.isEmpty
}
