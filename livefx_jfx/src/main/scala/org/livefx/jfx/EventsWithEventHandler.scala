package org.livefx.jfx

import javafx.event.Event
import org.livefx.EventSource
import javafx.event.EventHandler
import javafx.beans.property.ObjectProperty
import org.livefx.Events
import org.livefx.EventSource

class EventsWithEventHandler[E <: Event] extends EventSource[E] with EventHandler[E] {
  override def handle(event: E): Unit = publish(event)
}

object EventsWithEventHandler {
  def on[E <: Event](eventHandlerProperty: ObjectProperty[EventHandler[E]]): Events[E] = {
    eventHandlerProperty.get() match {
      case events: EventsWithEventHandler[E] => events
      case null =>
        val events = new EventsWithEventHandler[E]
        eventHandlerProperty.setValue(events)
        events
      case _ => throw new UnsupportedOperationException("event handler already installed")
    }
  }
}
