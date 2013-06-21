package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global

trait Dependency extends Spoilable {
  def value: Int
  
  def asliveValue: Dependency = this

  def map(f: Int => Int): Dependency = {
    val source = this
    new DependencyBinding {
      val ref = source.spoils.subscribe(spoilEvent => spoil(spoilEvent))

      protected override def computeValue: Int = f(source.value)
    }
  }

  def flatMap[B](f: Int => Dependency): Dependency = {
    val source = this
    val binding = new DependencyBinding {
      var nested: Dependency = f(source.value)
      val nestedSpoilHandler = { spoilEvent: Spoil => spoil(spoilEvent) }
      var nestedSubscription: Disposable = nested.spoils.subscribe(nestedSpoilHandler)
      val ref1 = source.spoils.subscribe { spoilEvent =>
        nestedSubscription.dispose()
        nested = f(source.value)
        nestedSubscription = nested.spoils.subscribe(nestedSpoilHandler)
        spoil(spoilEvent)
      }
      
      protected override def computeValue: Int = nested.value
    }
    binding
  }
}
