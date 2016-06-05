package org.livefx.core.syntax

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.livefx.core.disposal.Disposable

package object disposable {
  implicit class DisposableOps_YYKh2cf[A](val self: A) extends AnyVal {
    def dispose()(implicit ev: Disposable[A]): Unit = {
      try {
        ev.dispose(self)
      } catch {
        case e: Exception =>
      }
    }

    def asCloseable(implicit ev: Disposable[A]): Closeable = ev.asCloseable(self)

    def ++[B](that: B)(implicit evA: Disposable[A], evB: Disposable[B]): Closeable = {
      val disposableRefThat = new AtomicReference[B](that)
      val disposableRefThis = new AtomicReference[A](self)

      new Closeable {
        override def close(): Unit = {
          disposableRefThat.getAndSet(null.asInstanceOf[B]).dispose()
          disposableRefThis.getAndSet(null.asInstanceOf[A]).dispose()
        }
      }
    }
  }
}
