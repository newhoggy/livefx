package org.livefx

trait EventSink[-E] {
  def publish(event: E)(implicit strategy: PublishingStrategy): Unit
}
