package org.livefx

import org.livefx.script.Spoil

trait Spoilable extends Publisher {
  type Pub <: Spoilable

  protected def spoilsSource: EventSink[Spoil]

  def spoils: Events[Pub, Spoil]

  def spoiled: Boolean = false

  protected def spoil(spoilEvent: Spoil): Unit = spoilsSource.publish(spoilEvent)
}
