package org.livefx

import scala.concurrent._

trait Events[+E] { self =>
  def subscribeWeak(subscriber: E => Unit): Disposable
  def asEvents: Events[E] = this

  def +[F >: E](that: Events[F]): Events[F] = new Events[F] {
    def subscribeWeak(subscriber: F => Unit): Disposable = new Disposable {
      private var subscription1 = self.subscribeWeak(subscriber)
      private var subscription2 = that.subscribeWeak(subscriber)
      override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = for {
        _ <- subscription1.dispose()
        _ <- subscription2.dispose()
      } yield {
        subscription1 = null
        subscription2 = null
      }
    }
  }
}
