package org.livefx

import org.livefx.script.Change
import org.livefx.script.Message
import org.livefx.script.Spoil

trait LiveValue[@specialized(Boolean, Int, Long, Double) +A] extends Spoilable {
  type Pub <: LiveValue[A]
  
  def value: A
  
  def changes: Events[Pub, Change[A]]
  
  def asliveValue: LiveValue[A] = this

  def map[@specialized(Boolean, Int, Long, Double) B](f: A => B): LiveValue[B] = {
    val source = this
    new LiveBinding[B] {
      val ref = source.spoils.subscribeWeak((_, spoilEvent) => spoil(spoilEvent))

      protected override def computeValue: B = f(source.value)
    }
  }
  
  def flatMap[B](f: A => LiveValue[B]): LiveValue[B] = {
    val source = this
    val binding = new LiveBinding[B] {
      var nested: LiveValue[B] = f(source.value)
      val nestedSpoilHandler = { (_: Any, spoilEvent: Spoil) => spoil(spoilEvent) }
      var ref: Any = nested.spoils.subscribeWeak(nestedSpoilHandler)
      val ref1 = source.spoils.subscribeWeak { (_, spoilEvent) =>
        nested.spoils.unsubscribe(nestedSpoilHandler)
        nested = f(source.value)
        ref = nested.spoils.subscribeWeak(nestedSpoilHandler)
        spoil(spoilEvent)
      }
      
      protected override def computeValue: B = nested.value
    }
    binding
  }
  
  def spoilCount: LiveValue[Long] = {
    val outer = this
    new LiveBinding[Long] {
      private var counter = 0L
      private val ref = outer.spoils.subscribeWeak { (_, spoilEvent) =>
        counter += 1
        spoil(spoilEvent)
      }
      
      protected override def computeValue: Long = counter
    }
  }
}
