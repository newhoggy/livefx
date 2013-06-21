package org.livefx

import org.livefx.script.Spoil

abstract class DependencyBinding extends Dependency with Unspoilable {
  private lazy val _spoils = new DepthFirstEventSource[Spoil]
  
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
