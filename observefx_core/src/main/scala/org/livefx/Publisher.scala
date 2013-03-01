package org.livefx

import org.livefx.script.Message

trait Publisher[A] {
  type Pub <: Publisher[A]
  
  def publisher: Pub = this.asInstanceOf[Pub]
}
