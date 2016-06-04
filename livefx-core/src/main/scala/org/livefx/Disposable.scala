package org.livefx

import java.io.Closeable

trait Disposable extends Closeable {
  def dispose(): Unit

  final override def close(): Unit = dispose()

  def ++(that: Disposable): Disposable = {
    new Disposable {
      override def dispose(): Unit = {
        try {
          that.dispose()
        } catch {
          case e: Exception =>
        }

        try {
          this.dispose()
        } catch {
          case e: Exception =>
        }
      }
    }
  }
}
