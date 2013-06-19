package org.livefx

trait EventSink[-E] {
  def publish(event: E): Unit
}
