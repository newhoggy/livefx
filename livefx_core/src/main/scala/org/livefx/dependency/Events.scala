package org.livefx.dependency

import scala.concurrent._
import ExecutionContext.Implicits.global
import org.livefx.Disposable

trait Events[+E] { self =>
  def subscribe(subscriber: E => Unit): Disposable
  def asEvents: Events[E] = this

  def |[F >: E](that: Events[F]): Events[F] = new EventSource[F] with Disposable {
    private var subscription1 = self.subscribe(publish)
    private var subscription2 = that.subscribe(publish)

    override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = try {
      subscription1.dispose.flatMap(_ => subscription2.dispose)
    } finally {
      subscription1 = null
      subscription2 = null
    }
  }
  
  def impeded: Events[E] = new EventSource[E] with Disposable {
    private var stored = Option.empty[E]
    private var subscription = self.subscribe { e =>
      stored.foreach(publish(_))
      stored = Some(e)
    }

    override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = try {
      subscription.dispose
    } finally {
      subscription = null
    }
  }

  def map[F](f: E => F): Events[F] = new EventSource[F] with Disposable {
    private var subscription = self.subscribe(e => publish(f(e)))

    override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = try {
      subscription.dispose
    } finally {
      subscription = null
    }
  }

  def flatMap[F](f: E => Events[F]): Events[F] = new EventSource[F] with Disposable {
    private var mapped = Option.empty[Disposable]
    private var subscription = self.subscribe { e =>
      mapped.foreach(_.dispose)
      mapped = Some(f(e).subscribe(publish))
    }

    override protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = try {
      mapped.foldLeft(subscription.dispose)((x, y) => x.flatMap(_ => y.dispose))
    } finally {
      mapped = null
      subscription = null
    }
  }
}
