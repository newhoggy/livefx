package org.livefx.event

trait Sink[-E] {
  def publish(event: E): Unit
}
