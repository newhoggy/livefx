package org.livefx.dependency

import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.livefx.Disposable

trait Live[A] extends Spoilable { self =>
  def depth: Int
  
  def asliveValue: Live[A] = this

  def max(that: Live[A])(implicit ev: Ordering[A]): Live[A] = for (a <- this; b <- that) yield a max b
  
  def incremented: Live[A] = this.map(_ + 1)
  
  def map(f: Int => Int): Live[A] = new Binding[A] {
    val ref = self.spoils.subscribe(spoilEvent => spoil(spoilEvent))

    protected override def computeDepth: Int = f(self.depth)
  }

  def flatMap[B](f: Int => Live[B]): Live[B] = new Binding[B] {
    var nested: Live[B] = f(self.depth)
    var nestedSubscription: Disposable = nested.spoils.subscribe(spoil)
    val ref1 = self.spoils.subscribe { spoilEvent =>
      nestedSubscription.dispose()
      nested = f(self.depth)
      nestedSubscription = nested.spoils.subscribe(spoil)
      spoil(spoilEvent)
    }
    
    protected override def computeDepth: Int = nested.depth
  }
}
