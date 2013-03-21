package org.livefx

import org.livefx.script.Spoil

trait Unspoilable extends Spoilable {
  type Pub <: Unspoilable
  
  private var _spoiled: Boolean = true
  
  protected def unspoil(): Unit = _spoiled = false

  override def spoiled: Boolean = _spoiled

  protected override def spoil(spoilEvent: Spoil = Spoil()): Unit = if (!_spoiled) {
    _spoiled = true
    super.spoil(spoilEvent)
  }
}
