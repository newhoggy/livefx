package org.livefx

import org.livefx.script.Update
import org.livefx.script.NoLo

trait LiveBinding[A] extends LiveValue[A] with Unspoilable {
  type Pub <: LiveBinding[A]
  
  private var _value: A = null.asInstanceOf[A]
  
  def value: A = {
    if (spoiled) {
      val oldValue = _value
      val newValue = computeValue
      _value = newValue
      unspoil()
      changesSink.publish(Update(NoLo, oldValue, newValue))
    }
    
    _value
  }
  
  protected def computeValue: A
}
