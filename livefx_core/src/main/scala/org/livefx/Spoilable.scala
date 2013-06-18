package org.livefx

import org.livefx.script.Spoil

trait Spoilable extends Publisher {
  protected def spoilsSource: EventSink[Spoil]

  def spoils: Events[Spoil]

  def spoiled: Boolean = false

  protected def spoil(spoilEvent: Spoil): Unit = spoilsSource.publish(spoilEvent)
}
