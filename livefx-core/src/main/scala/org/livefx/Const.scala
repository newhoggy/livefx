package org.livefx

import org.livefx.disposal.{Disposable, Disposed}
import org.livefx.script.Change
import org.livefx.script.Spoil

case class Const[A](value: A) extends Live[A] {
  def spoils: EventSource[Spoil] = NoEvents

  def changes: EventSource[Change[A]] = NoEvents

  def spoilSink: org.livefx.EventSink[Spoil] = ???
}

object NoEvents extends EventSource[Nothing] with EventSink[Nothing] {
  override def subscribe(subscriber: Nothing => Unit): Disposable = Disposed
  override def publish(event: Nothing): Unit = Unit
}
