package org.livefx

import org.livefx.script.Message
import org.livefx.script.Update

trait LiveValue[A] extends Changeable[A, Update[A]] {
  type Pub <: LiveValue[A]
  
  def value: A
}
