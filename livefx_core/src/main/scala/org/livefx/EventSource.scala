package org.livefx

import scala.collection.mutable.WeakHashMap

class EventSource[E] extends Events[E] with EventSink[E] {
  private lazy val subscribers = new WeakHashMap[E => Unit, Option[E => Unit]]

  override def subscribe(subscriber: E => Unit): Disposable = new Disposable {
    subscribers += (subscriber -> Some(subscriber))
    
    override def dispose(): Unit = subscribers -= subscriber
  }

  override def subscribeWeak(subscriber: E => Unit): Disposable = new Disposable {
    subscribers += (subscriber -> None)
    
    override def dispose(): Unit = subscribers -= subscriber
  }

  override def unsubscribe(subscriber: E => Unit): Unit = { subscribers -= subscriber }

  override def publish(event: E): Unit = {
    subscribers.foreach { entry => entry._1(event) }
  }

  final def isEmpty: Boolean = subscribers.isEmpty
}
