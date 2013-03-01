package org.livefx

import org.livefx.script.Message

trait Publisher {
  type Pub <: Publisher
  
  def publisher: Pub = this.asInstanceOf[Pub]
}
