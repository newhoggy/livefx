package org.livefx.dependency

import org.livefx.script.Spoil

abstract class Binding extends Dependency with Renewable {
  private lazy val _spoils = new EventSource[Spoil]
  
  protected override def spoilSink: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Spoil] = _spoils

  private var _value: Int = 0
  
  override def value: Int = {
    if (spoiled) {
      val oldValue = _value
      val newValue = computeValue
      _value = newValue
      renew()
    }
    
    _value
  }

  protected def computeValue: Int
}
