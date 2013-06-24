package org.livefx.dependency

import org.livefx.script.Spoil
import org.livefx.Disposable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

trait Spoilable extends Publisher with Disposable {
  protected def spoilSink: EventSink[Spoil]

  def spoils: Events[Spoil]

  def spoiled: Boolean = false

  protected def spoil(spoilEvent: Spoil): Unit = if (live) spoilSink.publish(spoilEvent)
}
