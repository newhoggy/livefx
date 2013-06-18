package org.livefx

trait PublishingStrategy {
  def publishTo[E](subscribers: Iterable[E => Unit])(source: EventSource[E], event: E): Unit
}

object PublishingStrategy {
  object depthFirst extends PublishingStrategy {
    override def publishTo[E](subscribers: Iterable[E => Unit])(source: EventSource[E], event: E): Unit = {
      subscribers.foreach(s => s(event))
    }
  }
}
