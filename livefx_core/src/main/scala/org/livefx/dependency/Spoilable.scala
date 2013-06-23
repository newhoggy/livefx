package org.livefx.dependency

import org.livefx.script.Spoil

trait Spoilable extends Publisher {
  protected def spoilSink: EventSink[Spoil]

  def spoils: Events[Spoil]

  def spoiled: Boolean = false

  protected def spoil(spoilEvent: Spoil): Unit = spoilSink.publish(spoilEvent)
}
