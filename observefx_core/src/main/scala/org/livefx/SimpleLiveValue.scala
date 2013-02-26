package org.livefx

import org.livefx.script.Update
import org.livefx.script.NoLo

class SimpleLiveValue[A](private var _value: A) extends LiveValue[A] with SimpleSpoilable {
  def value: A = _value

  def value_=(newValue: A): Unit = {
    val oldValue = _value
    _value = newValue
    spoil()
    publish(Update(NoLo, oldValue, newValue))
  }
}
