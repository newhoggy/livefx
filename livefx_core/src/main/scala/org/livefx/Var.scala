package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil

class Var[A](@specialized(Boolean, Int, Long, Double) private var _value: A) extends Live[A] {
  type Pub <: Var[A]

  private lazy val _spoils = new EventSource[Pub, Spoil](publisher)
  
  protected override def spoilsSource: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Pub, Spoil] = _spoils

  lazy val _changes = new EventSource[Pub, Change[A]](publisher)

  override def changes: Events[Pub, Change[A]] = _changes

  def value: A = _value

  def value_=(newValue: A): Unit = updateValue(_value, newValue)
  
  protected def updateValue(oldValue: A, newValue: A): Unit = {
    _value = newValue
    spoil(Spoil())
    _changes.publish(Change(oldValue, newValue))
  }
}
