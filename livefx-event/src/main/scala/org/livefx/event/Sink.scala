package org.livefx.event

import java.io.Closeable

trait Sink[-A] extends Closeable { self =>
  def publish(event: A): Unit

  def comap[B](f: B => A): Sink[B] = new Sink[B] {
    override def publish(event: B): Unit = self.publish(f(event))

    override def close(): Unit = ()
  }
}

object Sink {
  val ignore = new Sink[Any] {
    override def publish(event: Any): Unit = ()

    override def close(): Unit = ()
  }
}
