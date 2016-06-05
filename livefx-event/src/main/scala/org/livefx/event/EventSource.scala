package org.livefx.event

import java.io.Closeable

import org.livefx.core.disposal.Disposable
import org.livefx.core.std.autoCloseable._
import org.livefx.core.syntax.disposable._

trait EventSource[+E] extends Closeable { self =>
  def subscribe(subscriber: E => Unit): Closeable
  def asEvents: EventSource[E] = this

  def |[F >: E](that: EventSource[F]): EventSource[F] = new EventBus[F] with Closeable {
    private val subscriptions = self.subscribe(publish) ++ that.subscribe(publish)

    override def close(): Unit = subscriptions.dispose()
  }
  
  def impeded: EventSource[E] = new EventBus[E] {
    private var stored = Option.empty[E]
    private var subscription = self.subscribe { e =>
      stored.foreach(publish(_))
      stored = Some(e)
    }
  }

  def map[F](f: E => F): EventSource[F] = new EventBus[F] {
    private var subscription = self.subscribe(e => publish(f(e)))

    override def close(): Unit = subscription.dispose()
  }

  def flatMap[F](f: E => EventSource[F]): EventSource[F] = new EventBus[F] with Closeable {
    private val mappedEvents = self.map(e => f(e))    
    private var mapped = Option.empty[Closeable]
    private var subscription = mappedEvents.subscribe { events =>
      mapped.foreach(_.dispose())
      mapped = Some(events.subscribe(publish))
    }

    override def close(): Unit = {
      mapped.foreach(_.dispose())
      subscription.dispose()
    }
  }
}

object EventSource {
  implicit def disposableEventSource_YYKh2cf[A] = new Disposable[EventSource[A]] {
    override protected def onDispose(a: EventSource[A]): Unit = {
      a.close()
    }
  }
}
