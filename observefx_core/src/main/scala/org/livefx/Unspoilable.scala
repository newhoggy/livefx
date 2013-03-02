package org.livefx

trait Unspoilable extends Spoilable {
  private var _spoiled: Boolean = true
  
  protected def unspoil(): Unit = _spoiled = false

  override def spoiled: Boolean = _spoiled

  protected override def spoil(): Unit = if (!_spoiled) {
    _spoiled = true
    super.spoil()
  }
}
