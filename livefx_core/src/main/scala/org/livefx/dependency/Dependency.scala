package org.livefx.dependency

import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.livefx.Disposable

trait Dependency extends Spoilable { self =>
  def depth: Int
  
  def asliveValue: Dependency = this

  def max(that: Dependency): Dependency = for (a <- this; b <- that) yield a max b
  
  def incremented: Dependency = this.map(_ + 1)
  
  def map(f: Int => Int): Dependency = {
    new Binding {
      val ref = self.spoils.subscribe(spoilEvent => spoil(spoilEvent))

      protected override def computeDepth: Int = f(self.depth)
    }
  }

  def flatMap[B](f: Int => Dependency): Dependency = {
    val binding = new Binding {
      var nested: Dependency = f(self.depth)
      val nestedSpoilHandler = { spoilEvent: Spoil => spoil(spoilEvent) }
      var nestedSubscription: Disposable = nested.spoils.subscribe(nestedSpoilHandler)
      val ref1 = self.spoils.subscribe { spoilEvent =>
        nestedSubscription.dispose()
        nested = f(self.depth)
        nestedSubscription = nested.spoils.subscribe(nestedSpoilHandler)
        spoil(spoilEvent)
      }
      
      protected override def computeDepth: Int = nested.depth
    }
    binding
  }
}
