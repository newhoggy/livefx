package org.livefx.dependency

import scala.concurrent._
import org.livefx.Disposable

trait Events[+E] { self =>
  def subscribe(subscriber: E => Unit): Disposable
  def asEvents: Events[E] = this

  def |[F >: E](that: Events[F]): Events[F] = new Events[F] {
    def subscribe(subscriber: F => Unit): Disposable = new Disposable {
      private var subscription1 = self.subscribe(subscriber)
      private var subscription2 = that.subscribe(subscriber)
      override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = for {
        _ <- subscription1.dispose()
        _ <- subscription2.dispose()
      } yield {
        subscription1 = null
        subscription2 = null
      }
    }
  }

  def map[F >: E](f: E => F): Events[F] = new Events[F] {
    def subscribe(subscriber: F => Unit): Disposable = new Disposable {
      private var subscription = self.subscribe(subscriber)
      override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = for {
        _ <- subscription.dispose()
      } yield {
        subscription = null
      }
    }
  }
}
