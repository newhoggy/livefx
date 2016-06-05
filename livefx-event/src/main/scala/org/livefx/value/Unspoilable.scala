package org.livefx.value

import org.livefx.value.script.Spoil

trait Unspoilable extends Spoilable {
  type Pub <: Unspoilable
  
  private var _spoiled: Boolean = true
  
  protected def unspoil(): Unit = _spoiled = false

  override def spoiled: Boolean = _spoiled

  protected override def spoil(spoilEvent: Spoil): Unit = if (!_spoiled) {
    _spoiled = true
    super.spoil(spoilEvent)
  }
}
