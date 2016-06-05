package org.livefx.event

import java.io.Closeable

trait Sink[-E] extends Closeable {
  def publish(event: E): Unit
}

object Sink {
  val ignore = new Sink[Any] {
    override def publish(event: Any): Unit = ()

    override def close(): Unit = ()
  }
}
