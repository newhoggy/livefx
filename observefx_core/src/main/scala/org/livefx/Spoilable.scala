package org.livefx

import org.livefx.script.Spoil

trait Spoilable extends Publisher {
  type Pub <: Spoilable
  
  lazy val _spoils = new EventSource[Spoilable, Spoil](this)
  
  def spoils: Events[Spoilable, Spoil] = _spoils
  
  def spoiled: Boolean = false
  
  protected def spoil(): Unit = _spoils.publish(Spoil)
}
