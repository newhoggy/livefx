package org.livefx

import org.livefx.script.Message
import org.livefx.script.Update

trait LiveValue[@specialized(Boolean, Int, Double) A] extends Changeable[A, Update[A]] with Spoilable {
  type Pub <: LiveValue[A]
  
  def value: A
  
  def map[@specialized(Boolean, Int, Double) B](f: A => B) = {
    val source = this
    new LiveBinding[B] {
      val ref = source.spoils.subscribeWeak((self, e) => spoil)
      
      protected override def computeValue: B = f(source.value)
    }
  }
}
