package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil
import org.livefx.dependency.Dependency
import org.livefx.dependency.Independent

case class Const[A](value: A) extends Live[A] {
  def spoils: Events[Spoil] = NoEvents

  def changes: Events[Change[A]] = NoEvents

  def spoilSink: org.livefx.EventSink[Spoil] = ???
}

object NoEvents extends Events[Nothing] with EventSink[Nothing] {
  override def dependency: Dependency = Independent
  override def subscribe(subscriber: Nothing => Unit): Disposable = Disposed
  override def publish(event: Nothing): Unit = Unit
}
