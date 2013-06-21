package org.livefx.dependency

import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.livefx.Disposable

trait Dependency extends Spoilable {
  def depth: Int
  
  def asliveValue: Dependency = this

  def max(that: Dependency): Dependency = for (a <- this; b <- that) yield a max b
  
  def incremented: Dependency = this.map(_ + 1)
  
  def map(f: Int => Int): Dependency = {
    val source = this
    new Binding {
      val ref = source.spoils.subscribe(spoilEvent => spoil(spoilEvent))

      protected override def computeDepth: Int = f(source.depth)
    }
  }

  def flatMap[B](f: Int => Dependency): Dependency = {
    val source = this
    val binding = new Binding {
      var nested: Dependency = f(source.depth)
      val nestedSpoilHandler = { spoilEvent: Spoil => spoil(spoilEvent) }
      var nestedSubscription: Disposable = nested.spoils.subscribe(nestedSpoilHandler)
      val ref1 = source.spoils.subscribe { spoilEvent =>
        nestedSubscription.dispose()
        nested = f(source.depth)
        nestedSubscription = nested.spoils.subscribe(nestedSpoilHandler)
        spoil(spoilEvent)
      }
      
      protected override def computeDepth: Int = nested.depth
    }
    binding
  }
}
