package org.livefx

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

trait Disposable extends Closeable {
  protected def onDispose(): Unit

  final def dispose(): Unit = {
    try {
      onDispose()
    } catch {
      case e: Exception =>
    }
  }

  final override def close(): Unit = dispose()

  def ++(that: Disposable): Disposable = {
    new Disposable {
      val disposableRefThat = new AtomicReference[Disposable](that)
      val disposableRefThis = new AtomicReference[Disposable](this)

      override def onDispose(): Unit = {
        disposableRefThat.getAndSet(Disposed).dispose()
        disposableRefThis.getAndSet(Disposed).dispose()
      }
    }
  }
}
