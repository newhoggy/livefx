package org.livefx

import scala.collection.immutable.HashSet
import org.livefx.util.TidyWeakReference
import org.livefx.util.TidyReferenceQueue
import scala.concurrent._
import ExecutionContext.Implicits.global

class EventSource[E] extends AbstractEventSource[E] {
  override def publish(event: E): Unit = {
    TidyReferenceQueue.tidy(1)
    subscribers.foreach(s => s(event))
  }
}
