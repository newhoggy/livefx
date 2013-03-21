package org.livefx

import org.livefx.script.Update
import org.livefx.script.NoLo
import org.livefx.script.Spoil

class SimpleLiveValue[A](@specialized(Boolean, Int, Long, Double) private var _value: A) extends LiveValue[A] {
  type Pub <: SimpleLiveValue[A]
  
  def value: A = _value

  def value_=(newValue: A): Unit = {
    val oldValue = _value
    _value = newValue
    spoil(Spoil())
    changesSink.publish(Update(NoLo, oldValue, newValue))
  }
}
