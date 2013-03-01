package org.livefx

import scala.collection.mutable.WeakHashMap

class EventSource[P, E](val publisher: P) extends Events[P, E] {
  private val subscribers = new HashSet[(P, E) => Unit]

  override def subscribe(subscriber: (P, E) => Unit): Unit = { subscribers += subscriber }
  override def unsubscribe(subscriber: (P, E) => Unit): Unit = { subscribers -= subscriber }

  def publish(event: E): Unit = subscribers.foreach(subscriber => subscriber(publisher, event))
}
