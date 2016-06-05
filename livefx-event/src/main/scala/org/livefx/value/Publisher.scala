package org.livefx.value

trait Publisher {
  type Pub <: Publisher
  
  def publisher: Pub = this.asInstanceOf[Pub]
}
