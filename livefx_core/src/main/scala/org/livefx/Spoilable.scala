package org.livefx

import org.livefx.script.Spoil

trait Spoilable extends Publisher {
  protected def spoilSink: EventSink[Spoil]

  def spoils: Events[Spoil]

  def spoiled: Boolean = false

  protected def spoil(spoilEvent: Spoil): Unit = {
    var done = false

    while (!done) {
      val oldRenewables = spoilEvent.renewables.get()
      if (spoilEvent.renewables.compareAndSet(oldRenewables, oldRenewables + this)) done = true
    }

    spoilSink.publish(spoilEvent)
  }
}
