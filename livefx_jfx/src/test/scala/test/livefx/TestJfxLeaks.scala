package test.livefx

import org.junit.Test
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import org.livefx.jfx.Observables.Implicits._
import org.junit.Assert
import javafx.beans.WeakInvalidationListener
import javafx.beans.property.SimpleIntegerProperty
import scala.ref.WeakReference
import scala.ref.ReferenceQueue

class TestJfxLeaks {
  @Test
  def testPropertyListenerLeaks(): Unit = {
    val refQueue = new ReferenceQueue[WeakInvalidationListener]
    var listener = new InvalidationListener {
      override def invalidated(observable: Observable): Unit = Unit
    }

    val property = new SimpleIntegerProperty(0)
    property.addListener(listener.weak: WeakInvalidationListener)
    
    val ref: Any = new WeakReference(listener, refQueue)
    listener = null
    
    System.gc()
    
    Assert.assertTrue(refQueue.poll.isEmpty)

    property.set(1)
    
    Assert.assertTrue(refQueue.poll.nonEmpty)
  }
//
//  @Test
//  def testPropertyListenerAfterInvalidationLeakFree(): Unit = {
//    val refQueue = new ReferenceQueue[WeakInvalidationListener]
//    val property = new SimpleIntegerProperty(0)
//    val listenerWeakRef = addListener(property, refQueue)
//    
//    System.gc()
//    
//    property.set(1)
//    
//    System.gc()
//    
//    
//    listenerWeakRef match {
//      case WeakReference(listener) => Assert.fail() // Invalidation should have unregistered listener
//      case _ => // Invalidation unregistered listener as expected
//    }
//  }
}
