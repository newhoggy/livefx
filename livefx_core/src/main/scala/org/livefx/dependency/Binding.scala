package org.livefx.dependency

import org.livefx.script.Spoil

abstract class Binding[A] extends Live[A] with Renewable {
  private lazy val _spoils = new EventSource[Spoil]
  
  protected override def spoilSink: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Spoil] = _spoils

  private var _depth: Option[A] = null
  
  override def value: A = {
    if (spoiled) {
      val oldDepth = _depth
      val newDepth = computeDepth
      _depth = Some(newDepth)
      renew()
    }

    _depth.get
  }

  protected def computeDepth: A
}
