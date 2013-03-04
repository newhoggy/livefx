package org.livefx

import org.livefx.script.Update
import org.livefx.script.NoLo

class SimpleLiveValue[A](@specialized(Boolean, Int, Double) private var _value: A) extends LiveValue[A] {
  type Pub <: SimpleLiveValue[A]
  
  def value: A = _value

  def value_=(newValue: A): Unit = {
    val oldValue = _value
    _value = newValue
    spoil()
    changesSink.publish(Update(NoLo, oldValue, newValue))
  }
}
