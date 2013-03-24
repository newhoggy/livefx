package org.livefx

import org.livefx.script.Update
import org.livefx.script.NoLo

abstract class LiveBinding[A] extends LiveValue[A] with LiveValueHasChangeSink[A] with Unspoilable {
  type Pub <: LiveBinding[A]

  lazy val _updates = new EventSource[Pub, Update[A]](publisher)

  override def updates: Events[Pub, Update[A]] = _updates

  private var _value: A = null.asInstanceOf[A]
  
  override def value: A = {
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
