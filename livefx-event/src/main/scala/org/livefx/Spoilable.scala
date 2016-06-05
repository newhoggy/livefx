package org.livefx

import org.livefx.event.{EventSink, EventSource}
import org.livefx.value.script.Spoil

trait Spoilable extends Publisher {
  protected def spoilSink: EventSink[Spoil]

  def spoils: EventSource[Spoil]

  def spoiled: Boolean = false

  protected def spoil(spoilEvent: Spoil): Unit = spoilSink.publish(spoilEvent)
}
