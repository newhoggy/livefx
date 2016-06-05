package org.livefx.disposal

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
    val disposableRefThat = new AtomicReference[Disposable](that)
    val disposableRefThis = new AtomicReference[Disposable](this)

    new Disposable {
      override def onDispose(): Unit = {
        disposableRefThat.getAndSet(Disposed).dispose()
        disposableRefThis.getAndSet(Disposed).dispose()
      }
    }
  }
}

object Disposable {
  def apply(a: AutoCloseable): Disposable = new Disposable {
    override protected def onDispose(): Unit = a.close()
  }
}
