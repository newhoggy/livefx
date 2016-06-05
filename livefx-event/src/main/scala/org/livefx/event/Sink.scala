package org.livefx.event

import org.livefx.core.disposal.Disposer

trait Sink[-A] extends Disposer { self =>
  /** Publish an event to a sink.
    */
  def publish(event: A): Unit

  /** Create a new Sink that applies a function to the event before propagating it to the
    * original sink.
    */
  def comap[B](f: B => A): Sink[B] = new Sink[B] {
    override def publish(event: B): Unit = self.publish(f(event))
  }
}

object Sink {
  /** A sink that ignores values published to it.
    */
  val ignore = new Sink[Any] {
    override def publish(event: Any): Unit = ()
  }
}
