package org.livefx

import org.livefx.script.Message

trait LiveValue[A] extends Changeable[A] {
  type Pub <: LiveValue[A]
  
  def value: A
}
