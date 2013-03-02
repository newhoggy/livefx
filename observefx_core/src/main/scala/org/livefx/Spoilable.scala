package org.livefx

import org.livefx.script.Spoil

trait Spoilable extends Publisher {
  type Pub <: Spoilable
  
  lazy val _spoils = new EventSource[Pub, Spoil](publisher)
  
  def spoils: Events[Pub, Spoil] = _spoils
  
  def spoiled: Boolean = false
  
  protected def spoil(): Unit = _spoils.publish(Spoil)
}
