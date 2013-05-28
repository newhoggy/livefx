package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil

case class Const[A](value: A) extends LiveValue[A] {
  type Pub <: Const[A]

  def spoils: Events[Pub, Spoil] = NoEvents

  def changes: Events[Pub, Change[A]] = NoEvents

  def spoilsSource: org.livefx.EventSink[org.livefx.script.Spoil] = ???
}

object NoEvents extends Events[Nothing, Nothing] with EventSink[Nothing] {
  override def subscribe(subscriber: (Nothing, Nothing) => Unit): Disposable = Disposed
  override def subscribeWeak(subscriber: (Nothing, Nothing) => Unit): Disposable = Disposed
  override def unsubscribe(subscriber: (Nothing, Nothing) => Unit): Unit = Disposed
  override def publish(event: Nothing): Unit = Unit
}
