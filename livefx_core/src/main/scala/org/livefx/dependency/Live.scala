package org.livefx.dependency

import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.livefx.Disposable

trait Live[A] extends Spoilable { self =>
  def value: A
  
  def asliveValue: Live[A] = this

  def max(that: Live[A])(implicit ev: Ordering[A]): Live[A] = for (a <- this; b <- that) yield Ordering[A].max(a, b)
  
  def map(f: A => A): Live[A] = new Binding[A] {
    val ref = self.spoils.subscribe(spoilEvent => spoil(spoilEvent))

    protected override def computeDepth: A = f(self.value)
  }

  def flatMap[B](f: A => Live[B]): Live[B] = new Binding[B] {
    private var nested: Live[B] = f(self.value)
    
    private val nestedSubscription = self.spoils.flatMap { _ =>
      nested = f(self.value)
      nested.spoils
    }.subscribe(spoil)
    
    protected override def computeDepth: B = nested.value
  }
}
