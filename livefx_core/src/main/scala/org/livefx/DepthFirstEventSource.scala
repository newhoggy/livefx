package org.livefx

import org.livefx.util.TidyReferenceQueue

class DepthFirstEventSource[E] extends EventSource[E] {
  override def publish(event: E): Unit = {
    TidyReferenceQueue.tidy(1)
    subscribers.foreach(s => s(event))
  }
}
