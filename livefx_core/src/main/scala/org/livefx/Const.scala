package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil

case class Const[A](value: A) extends Live[A] {
  def spoils: Events[Spoil] = NoEvents

  def changes: Events[Change[A]] = NoEvents

  def spoilsSource: org.livefx.EventSink[org.livefx.script.Spoil] = ???
}

object NoEvents extends Events[Nothing] with EventSink[Nothing] {
  override def subscribe(subscriber: Nothing => Unit): Disposable = Disposed
  override def subscribeWeak(subscriber: Nothing => Unit): Disposable = Disposed
  override def unsubscribe(subscriber: Nothing => Unit): Unit = Disposed
  override def publish(event: Nothing): Unit = Unit
}
