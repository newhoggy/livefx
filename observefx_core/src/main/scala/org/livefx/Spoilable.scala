package org.livefx

import org.livefx.script.Spoil

trait Spoilable extends Publisher {
  type Pub <: Spoilable
  
  lazy val _spoils = new EventSource[Spoilable, Spoil](this)
  
  def spoils: Events[Spoilable, Spoil] = _spoils

  private var _spoiled: Boolean = true
  
  protected def spoil(): Unit = if (!_spoiled) {
    _spoiled = true
    _spoils.publish(Spoil)
  }
  
  protected def unspoil(): Unit = _spoiled = false
}
