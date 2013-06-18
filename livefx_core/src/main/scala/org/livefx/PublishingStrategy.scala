package org.livefx

trait PublishingStrategy {
  def publishTo[E](subscribers: Iterable[E => Unit])(event: E): Unit
}

object PublishingStrategy {
  object depthFirst extends PublishingStrategy {
    def publishTo[E](subscribers: Iterable[E => Unit])(event: E): Unit = subscribers.foreach(s => s(event))
  }
}
