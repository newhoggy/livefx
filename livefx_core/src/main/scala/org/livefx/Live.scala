package org.livefx

import org.livefx.script.Change
import org.livefx.script.Message
import org.livefx.script.Spoil

trait Live[@specialized(Boolean, Int, Long, Double) +A] extends Spoilable {
  type Pub <: Live[A]
  
  def value: A
  
  def changes: Events[Pub, Change[A]]
  
  def asliveValue: Live[A] = this

  def map[@specialized(Boolean, Int, Long, Double) B](f: A => B): Live[B] = {
    val source = this
    new Binding[B] {
      val ref = source.spoils.subscribeWeak((_, spoilEvent) => spoil(spoilEvent))

      protected override def computeValue: B = f(source.value)
    }
  }
  
  def flatMap[B](f: A => Live[B]): Live[B] = {
    val source = this
    val binding = new Binding[B] {
      var nested: Live[B] = f(source.value)
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
  
  def spoilCount: Live[Long] = {
    val outer = this
    new Binding[Long] {
      private var counter = 0L
      private val ref = outer.spoils.subscribeWeak { (_, spoilEvent) =>
        counter += 1
        spoil(spoilEvent)
      }
      
      protected override def computeValue: Long = counter
    }
  }
}
