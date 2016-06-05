package org.livefx.event

import java.io.Closeable
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

import org.livefx.core.disposal.OnClose
import org.livefx.core.syntax.std.atomicReference._

trait SinkSource[A, B] extends Sink[A] with Source[B]

object SinkSource {
  def apply[A, B](f: A => B): SinkSource[A, B] = {
    new SinkSource[A, B] {
      private final val subscribers = new AtomicReference(List.empty[WeakReference[B => Unit]])
      private final val garbage = new AtomicInteger(0)

      final override def publish(event: A): Unit = {
        subscribers.get().foreach { subscriberRef =>
          val subscriber = subscriberRef.get()

          if (subscriber != null) {
            subscriber(f(event))
          } else {
            garbage.incrementAndGet()
          }
        }

        houseKeep()
      }

      final override def subscribe(subscriber: B => Unit): Closeable = {
        val subscriberRef = new WeakReference(subscriber)

        subscribers.update(subscriberRef :: _)

        houseKeep()

        OnClose {
          identity(subscriber)
          subscriberRef.clear()
          houseKeep()
        }
      }

      final override def close(): Unit = ()

      final def houseKeep(): Unit = {
        if (garbage.get() > subscribers.get().size) {
          garbage.set(0)
          subscribers.update { subscriptions =>
            subscriptions.filter { subscription =>
              subscription.get() != null
            }
          }
        }
      }
    }
  }
}
