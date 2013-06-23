package org.livefx.dependency

import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.livefx.Disposable
import org.livefx.Disposed

trait Live[A] extends Spoilable { self =>
  def value: A
  
  def asliveValue: Live[A] = this

  def max(that: Live[A])(implicit ev: Ordering[A]): Live[A] = for (a <- this; b <- that) yield Ordering[A].max(a, b)
  
  def map(f: A => A): Live[A] = new Binding[A] {
    private val ref = self.spoils.subscribe(spoil)

    protected override def computeValue: A = f(self.value)
  }

  def flatMap[B](f: A => Live[B]): Live[B] = new Binding[B] {
    private var nested: Live[B] = null
    
    private var childSubscription = self.spoils.subscribe { e =>
      nested = null
      spoil(e)
    }
    
    private val valueSubscription: Disposable = Disposed
    
    protected override def computeValue: B = {
      if (nested == null) {
        nested = f(self.value)
        childSubscription.dispose
        childSubscription = nested.spoils.subscribe(spoil)
      }
      
      nested.value
    }
  }
}
