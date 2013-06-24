package org.livefx.dependency

import org.livefx.script.Spoil

trait Binding[A] extends Live[A] {
  type Pub <: Renewable
  
  private var _spoiled: Boolean = true
  
  protected def renew(): Unit = _spoiled = false

  override def spoiled: Boolean = _spoiled

  protected override def spoil(spoilEvent: Spoil): Unit = if (!_spoiled) {
    _spoiled = true
    super.spoil(spoilEvent)
  }

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
