package org.livefx.value

import java.io.Closeable

import org.livefx.core.disposal.Closed
import org.livefx.event.{EventSink, EventSource}
import org.livefx.value.script.{Change, Spoil}

case class Const[A](value: A) extends Live[A] {
  def spoils: EventSource[Spoil] = NoEvents

  def changes: EventSource[Change[A]] = NoEvents

  def spoilSink: EventSink[Spoil] = ???
}

object NoEvents extends EventSource[Nothing] with EventSink[Nothing] {
  override def subscribe(subscriber: Nothing => Unit): Closeable = Closed
  override def publish(event: Nothing): Unit = Unit

  override def close(): Unit = ()
}
