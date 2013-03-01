package org.livefx

import org.livefx.script.Spoil

trait Spoilable[A] {
  lazy val _spoils = new EventSource[Spoilable[A], Spoil](this)
  
  def spoils: Events[Spoilable[A], Spoil] = _spoils

  private var _spoiled: Boolean = true
  
  protected def spoil(): Unit = if (!_spoiled) {
    _spoiled = true
    _spoils.publish(Spoil)
  }
  
  protected def unspoil(): Unit = _spoiled = false
}
