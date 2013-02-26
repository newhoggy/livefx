package org.livefx

trait Publisher[Evt] {
  type Pub <: Publisher[Evt]
  type Sub = Subscriber[Evt, Pub]
  type Filter = Evt => Boolean

  /** The publisher itself of type `Pub`. Implemented by a cast from `this` here.
   *  Needs to be overridden if the actual publisher is different from `this`.
   */
  protected val self: Pub = this.asInstanceOf[Pub]

  private val subscribers = new HashSet[Sub]

  def subscribe(sub: Sub) { subscribers += sub }
  def unsubscribe(sub: Sub) { subscribers -= sub }

  protected def publish(event: Evt): Unit = {
    subscribers.foreach(sub => sub.notify(self, event))
  }
}
