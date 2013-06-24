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
    private var child: Live[B] = null
    
    private val childSubscription: Disposable = self.spoils.subscribe { e =>
      child = null
      valueSubscription.dispose
      spoil(e)
    }
    
    private var valueSubscription: Disposable = Disposed
    
    protected override def computeValue: B = {
      if (child == null) {
        child = f(self.value)
        valueSubscription = child.spoils.subscribe(spoil)
      }
      
      child.value
    }
  }
}