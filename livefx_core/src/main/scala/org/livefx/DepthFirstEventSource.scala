package org.livefx

import org.livefx.util.TidyReferenceQueue

class DepthFirstEventSource[E] extends AbstractEventSource[E] {
  override def publish(event: E): Unit = {
    TidyReferenceQueue.tidy(1)
    subscribers.foreach(s => s(event))
  }
}
