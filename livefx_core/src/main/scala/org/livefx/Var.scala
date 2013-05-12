package org.livefx

import org.livefx.script.Update
import org.livefx.script.NoLo
import org.livefx.script.Spoil

class Var[A](@specialized(Boolean, Int, Long, Double) private var _value: A) extends LiveValue[A] {
  type Pub <: Var[A]

  lazy val _updates = new EventSource[Pub, Update[A]](publisher)

  override def updates: Events[Pub, Update[A]] = _updates

  def value: A = _value

  def value_=(newValue: A): Unit = updateValue(_value, newValue)
  
  protected def updateValue(oldValue: A, newValue: A): Unit = {
    _value = newValue
    spoil(Spoil())
    _updates.publish(Update(NoLo, oldValue, newValue))
  }
}
