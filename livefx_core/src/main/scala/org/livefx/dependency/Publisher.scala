package org.livefx.dependency

trait Publisher {
  type Pub <: Publisher
  
  def publisher: Pub = this.asInstanceOf[Pub]
}
