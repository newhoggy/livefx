package org.livefx.event

trait EventSink[-E] {
  def publish(event: E): Unit
}
