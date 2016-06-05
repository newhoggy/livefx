package org.livefx.event

trait Sink[-E] {
  def publish(event: E): Unit
}

object Sink {
  val ignore = new Sink[Nothing] {
    override def publish(event: Nothing): Unit = ()
  }
}
