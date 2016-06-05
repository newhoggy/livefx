package org.livefx

import java.io.Closeable

import org.livefx.disposal.{Closed, Disposable}
import org.livefx.script.Change

import scala.concurrent.ExecutionContext.Implicits.global

trait Live[@specialized(Boolean, Char, Byte, Short, Int, Long, Double) +A] extends Spoilable { self =>
  def value: A
  
  def asliveValue: Live[A] = this

  def changes: EventSource[Change[A]]
  
  def map[@specialized(Boolean, Char, Byte, Short, Int, Long, Double) B](f: A => B): Live[B] = new Binding[B] {
    val ref = self.spoils.subscribe(spoil)

    protected override def computeValue: B = f(self.value)
  }

  def flatMap[B](f: A => Live[B]): Live[B] = {
    var child: Live[B] = null
    var valueSubscription: Closeable = Closed
    
    new Binding[B] {
      private val childSubscription: Closeable = self.spoils.subscribe { e =>
        child = null
        valueSubscription.close()
        spoil(e)
      }
      
      protected override def computeValue: B = {
        if (child == null) {
          child = f(self.value)
          valueSubscription = child.spoils.subscribe(spoil)
        }
        
        child.value
      }
    }
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
