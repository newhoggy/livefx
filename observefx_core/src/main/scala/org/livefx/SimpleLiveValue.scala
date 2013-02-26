package org.livefx

class SimpleLiveValue[A](private var _value: A) extends LiveValue[A] with SimpleSpoilable {
  def value: A = _value

  def value_=(newValue: A): Unit = _value = newValue
}
