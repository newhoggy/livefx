package org.livefx.dependency

import org.livefx.script.Spoil
import org.livefx.EventSink
import org.livefx.Events
import org.livefx.Unspoilable

abstract class DependencyBinding extends Dependency with Unspoilable {
  private lazy val _spoils = new DependencyEventSource[Spoil]
  
  protected override def spoilSink: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Spoil] = _spoils

  private var _value: Int = 0
  
  override def value: Int = {
    if (spoiled) {
      val oldValue = _value
      val newValue = computeValue
      _value = newValue
      unspoil()
    }
    
    _value
  }

  protected def computeValue: Int
}
