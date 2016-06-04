package org.livefx

trait Publisher {
  type Pub <: Publisher
  
  def publisher: Pub = this.asInstanceOf[Pub]
}
