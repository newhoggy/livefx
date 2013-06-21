package org.livefx.dependency

import org.livefx.script.Spoil

abstract class Binding extends Dependency with Renewable {
  private lazy val _spoils = new EventSource[Spoil]
  
  protected override def spoilSink: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Spoil] = _spoils

  private var _depth: Int = 0
  
  override def depth: Int = {
    if (spoiled) {
      val oldDepth = _depth
      val newDepth = computeDepth
      _depth = newDepth
      renew()
    }

    _depth
  }

  protected def computeDepth: Int
}
