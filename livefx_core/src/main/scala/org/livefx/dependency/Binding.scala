package org.livefx.dependency

import org.livefx.script.Spoil

abstract class Binding[A] extends Live[A] with Renewable {
  private lazy val _spoils = new EventSource[Spoil]
  
  protected override def spoilSink: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Spoil] = _spoils

  private var _value: Option[A] = null
  
  override def value: A = {
    if (spoiled) {
      val oldDepth = _value
      val newDepth = computeValue
      _value = Some(newDepth)
      renew()
    }

    _value.get
  }

  protected def computeValue: A
}
