package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil

case class Const[A](value: A) extends Live[A] {
  def spoils: Events[Spoil] = NoEvents

  def changes: Events[Change[A]] = NoEvents

  def spoilSink: org.livefx.EventSink[Spoil] = ???
}

object NoEvents extends Events[Nothing] with EventSink[Nothing] {
  override def subscribe(subscriber: Nothing => Unit): Dependency = Dependency.None
  override def publish(event: Nothing): Unit = Unit
}
