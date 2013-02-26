package org.livefx

import org.livefx.script.Spoil

trait Publisher[Evt] {
  type Pub <: Publisher[Evt]
  type Filter = Evt => Boolean

  /**
   * The publisher itself of type `Pub`. Implemented by a cast from `this` here.
   * Needs to be overridden if the actual publisher is different from `this`.
   */
  protected val self: Pub = this.asInstanceOf[Pub]

  private val subscribers = new HashSet[(Pub, Evt) => Unit]

  def subscribe(sub: (Pub, Evt) => Unit): Unit = { subscribers += sub }
  def unsubscribe(sub: (Pub, Evt) => Unit): Unit = { subscribers -= sub }

  protected def publish(event: Evt): Unit = subscribers.foreach(sub => sub(self, event))
}
