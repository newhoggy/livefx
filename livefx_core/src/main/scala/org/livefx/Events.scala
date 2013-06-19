package org.livefx

import scala.concurrent._

trait Events[+E] { self =>
  def subscribe(subscriber: E => Unit): Disposable
  def subscribeWeak(subscriber: E => Unit): Disposable
  def unsubscribe(subscriber: E => Unit): Unit
  def asEvents: Events[E] = this

  def +[F >: E](that: Events[F]): Events[F] = new Events[F] {
    def subscribe(subscriber: F => Unit): Disposable = new Disposable {
      val disposable1 = self.subscribe(subscriber)
      val disposable2 = that.subscribe(subscriber)
      override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = for {
        _ <- disposable1.dispose()
        _ <- disposable2.dispose()
      } yield Unit
    }

    def subscribeWeak(subscriber: F => Unit): Disposable = new Disposable {
      val disposable1 = self.subscribe(subscriber)
      val disposable2 = that.subscribe(subscriber)
      override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = for {
        _ <- disposable1.dispose()
        _ <- disposable2.dispose()
      } yield Unit
    }

    def unsubscribe(subscriber: F => Unit): Unit = {
      self.unsubscribe(subscriber)
      that.unsubscribe(subscriber)
    }
  }
}
