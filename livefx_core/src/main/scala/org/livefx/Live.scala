package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.livefx.{dependency => dep}

trait Live[@specialized(Boolean, Int, Long, Double) +A] extends Spoilable { self =>
  def value: A
  
  def changes: Events[Change[A]]
  
  def asliveValue: Live[A] = this

  def map[@specialized(Boolean, Int, Long, Double) B](f: A => B): Live[B] = new Binding[B] {
    override val dependency: dep.Live[Int] = self.spoils.dependency.incremented
    val ref = self.spoils.subscribe(spoilEvent => spoil(spoilEvent))

    protected override def computeValue: B = f(self.value)
  }

  def flatMap[B](f: A => Live[B]): Live[B] = {
    val binding = new Binding[B] {
      override val dependency: dep.Live[Int] = self.spoils.dependency.incremented // TODO also must include nested
      var nested: Live[B] = f(self.value)
      var nestedSubscription: Disposable = nested.spoils.subscribe(spoil)
      val subscription = self.spoils.subscribe { spoilEvent =>
        nestedSubscription.dispose()
        nested = f(self.value)
        nestedSubscription = nested.spoils.subscribe(spoil)
        spoil(spoilEvent)
      }
      
      protected override def computeValue: B = nested.value
    }
    binding
  }

  def spoilCount: Live[Long] = {
    new Binding[Long] {
      override def dependency: dep.Live[Int] = self.spoils.dependency.incremented
      private var counter = 0L
      private val ref = self.spoils.subscribe { spoilEvent =>
        counter += 1
        spoil(spoilEvent)
      }

      protected override def computeValue: Long = counter
    }
  }
}
