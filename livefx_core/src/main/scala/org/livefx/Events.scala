package org.livefx

import scala.concurrent._
import org.livefx.dependency.Dependency

trait Events[+E] { self =>
  def dependency: Dependency
  def subscribe(subscriber: E => Unit): Disposable
  def asEvents: Events[E] = this

  def |[F >: E](that: Events[F]): Events[F] = new Events[F] {
    override val dependency: Dependency = (self.dependency max that.dependency).incremented
    override def subscribe(subscriber: F => Unit): Disposable = new Disposable {
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
    override val dependency: Dependency = self.dependency.incremented
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
