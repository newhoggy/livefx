package org.livefx.core.disposal

import java.io.Closeable

object OnClose {
  def apply(f: => Unit): Closeable = {
    new Closeable {
      override def close(): Unit = f
    }
  }
}
