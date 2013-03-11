package org.livefx

import scala.collection.mutable.WeakHashMap

class EventSource[P, E](val publisher: P) extends Events[P, E] with EventSink[E] {
  private lazy val subscribers = new WeakHashMap[(P, E) => Unit, Option[(P, E) => Unit]]

  override def subscribe(subscriber: (P, E) => Unit): Disposable = new Disposable {
    subscribers += (subscriber -> Some(subscriber))
    
    override def dispose(): Unit = subscribers -= subscriber
  }

  override def subscribeWeak(subscriber: (P, E) => Unit): Disposable = new Disposable {
    subscribers += (subscriber -> None)
    
    override def dispose(): Unit = subscribers -= subscriber
  }

  override def unsubscribe(subscriber: (P, E) => Unit): Unit = { subscribers -= subscriber }

  override def publish(event: E): Unit = subscribers.foreach { entry => entry._1(publisher, event) }
}
