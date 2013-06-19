package org.livefx

import org.livefx.util.TidyReferenceQueue

trait DepthFirst[E] { self: EventSource[E] =>
  def publish(event: E): Unit = {
    TidyReferenceQueue.tidy(1)
    subscribers.foreach(s => s(event))
  }
}
