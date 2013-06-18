package org.livefx

import org.livefx.script.Change
import org.livefx.script.Validity
import org.livefx.script.Invalid

trait Var[@specialized(Boolean, Int, Long, Double) A] extends Live[A] {
  private lazy val _spoils = new EventSource[Validity]

  protected override def spoilsSource: EventSink[Validity] = _spoils

  override def spoils: Events[Validity] = _spoils

  lazy val _changes = new EventSource[Change[A]]

  override def changes: Events[Change[A]] = _changes

  def value: A

  def value_=(newValue: A): Unit

  def :=(value: A): this.type = {
    this.value = value
    this
  }
}

object Var {
  def apply[@specialized(Boolean, Int, Long, Double) A](initialValue: A): Var[A] = new Var[A] {
    var _value: A = initialValue

    override def value: A = _value

    override def value_=(newValue: A): Unit = updateValue(_value, newValue)

    protected def updateValue(oldValue: A, newValue: A): Unit = {
      _value = newValue
      spoil(Invalid())
      _changes.publish(Change(oldValue, newValue))
    }
  }
}
