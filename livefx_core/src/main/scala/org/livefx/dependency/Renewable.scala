package org.livefx.dependency

import org.livefx.script.Spoil

trait Renewable extends Spoilable {
  type Pub <: Renewable
  
  private var _spoiled: Boolean = true
  
  protected def renew(): Unit = _spoiled = false

  override def spoiled: Boolean = _spoiled

  protected override def spoil(spoilEvent: Spoil): Unit = if (!_spoiled) {
    _spoiled = true
    super.spoil(spoilEvent)
  }
}
