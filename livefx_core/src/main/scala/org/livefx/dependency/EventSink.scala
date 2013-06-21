package org.livefx.dependency

trait EventSink[-E] {
  def publish(event: E): Unit
}
