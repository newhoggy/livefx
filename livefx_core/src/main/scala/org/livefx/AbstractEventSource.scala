package org.livefx

import scala.collection.immutable.HashSet
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.future

import org.livefx.util.TidyReferenceQueue
import org.livefx.util.TidyWeakReference

trait AbstractEventSource[E] extends Events[E] with EventSink[E] {
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

  def publish(event: E): Unit
}
