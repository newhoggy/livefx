package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil
import scala.concurrent._
import ExecutionContext.Implicits.global

trait Live[@specialized(Boolean, Int, Long, Double) +A] extends Spoilable { self =>
  def value: A
  
  def asliveValue: Live[A] = this

  def changes: Events[Change[A]]
  
  def map[@specialized(Boolean, Int, Long, Double) B](f: A => B): Live[B] = new Binding[B] {
    val ref = self.spoils.subscribe(spoil)

    protected override def computeValue: B = f(self.value)
  }

  def flatMap[B](f: A => Live[B]): Live[B] = {
    val binding = new Binding[B] {
      var child: Live[B] = null
      
      private val childSubscription: Disposable = self.spoils.subscribe { e =>
        child = null
        valueSubscription.dispose
        spoil(e)
      }
      
      var valueSubscription: Disposable = Disposed
      
      protected override def computeValue: B = {
        if (child == null) {
          child = f(self.value)
          valueSubscription = child.spoils.subscribe(spoil)
        }
        
        child.value
      }
    }
    binding
  }

  def spoilCount: Live[Long] = {
    new Binding[Long] {
      private var counter = 0L
      private val ref = self.spoils.subscribe { spoilEvent =>
        counter += 1
        spoil(spoilEvent)
      }

      protected override def computeValue: Long = counter
    }
  }
}
