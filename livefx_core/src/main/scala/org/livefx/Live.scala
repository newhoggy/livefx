package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global

trait Live[@specialized(Boolean, Int, Long, Double) +A] extends Spoilable {
  def value: A
  
  def changes: Events[Change[A]]
  
  def asliveValue: Live[A] = this

  def map[@specialized(Boolean, Int, Long, Double) B](f: A => B): Live[B] = {
    val source = this
    new Binding[B] {
      val ref = source.spoils.subscribe(spoilEvent => spoil(spoilEvent))

      protected override def computeValue: B = f(source.value)
    }
  }

  def flatMap[B](f: A => Live[B]): Live[B] = {
    val source = this
    val binding = new Binding[B] {
      var nested: Live[B] = f(source.value)
      val nestedSpoilHandler = { spoilEvent: Spoil => spoil(spoilEvent) }
      var nestedSubscription: Disposable = nested.spoils.subscribe(nestedSpoilHandler)
      val ref1 = source.spoils.subscribe { spoilEvent =>
        nestedSubscription.dispose()
        nested = f(source.value)
        nestedSubscription = nested.spoils.subscribe(nestedSpoilHandler)
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
      private val ref = outer.spoils.subscribe { spoilEvent =>
        counter += 1
        spoil(spoilEvent)
      }

      protected override def computeValue: Long = counter
    }
  }
}
