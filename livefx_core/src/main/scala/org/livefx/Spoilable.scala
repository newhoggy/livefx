package org.livefx

import org.livefx.script.Validity

trait Spoilable extends Publisher {
  protected def spoilsSource: EventSink[Validity]

  def spoils: Events[Validity]

  def spoiled: Boolean = false

  protected def spoil(spoilEvent: Validity): Unit = spoilsSource.publish(spoilEvent)
}
