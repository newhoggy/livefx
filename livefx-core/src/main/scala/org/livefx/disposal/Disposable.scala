package org.livefx.disposal

import java.io.Closeable

trait Disposable[-A] {
  protected def onDispose(a: A): Unit

  def asCloseable(a: A): Closeable = new Closeable {
    override def close(): Unit = onDispose(a)
  }

  final def dispose(a: A): Unit = try onDispose(a) catch { case e: Exception => }
}
