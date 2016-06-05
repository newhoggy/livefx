package org.livefx.value

import org.livefx.event.{EventBus, EventSink, EventSource}
import org.livefx.value.script.{Change, Spoil}

trait Var[@specialized(Boolean, Char, Byte, Short, Int, Long, Double) A] extends Live[A] {
  private lazy val _spoils = new EventBus[Spoil]

  protected override def spoilSink: EventSink[Spoil] = _spoils

  override def spoils: EventSource[Spoil] = _spoils

  lazy val _changes = new EventBus[Change[A]]

  override def changes: EventSource[Change[A]] = _changes

  def value: A

  def value_=(newValue: A): Unit

  def :=(value: A): this.type = {
    this.value = value
    this
  }
}

object Var {
  def apply[@specialized(Boolean, Char, Byte, Short, Int, Long, Double) A](initialValue: A): Var[A] = new Var[A] {
    var _value: A = initialValue

    override def value: A = _value

    override def value_=(newValue: A): Unit = updateValue(_value, newValue)

    protected def updateValue(oldValue: A, newValue: A): Unit = {
      _value = newValue
      spoil(Spoil())
      _changes.publish(Change(oldValue, newValue))
    }
  }
}
