package org.livefx.value

import org.livefx.event.{Sink, Source}
import org.livefx.value.script.Spoil

trait Spoilable extends Publisher {
  protected def spoilSink: Sink[Spoil]

  def spoils: Source[Spoil]

  def spoiled: Boolean = false

  protected def spoil(spoilEvent: Spoil): Unit = spoilSink.publish(spoilEvent)
}
