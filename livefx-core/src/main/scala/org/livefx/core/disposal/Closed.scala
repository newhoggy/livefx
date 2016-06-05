package org.livefx.core.disposal

import java.io.Closeable

object Closed extends Closeable {
  final override def close(): Unit = ()
}
