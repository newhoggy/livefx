package test.livefx

import org.junit.Test
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import org.livefx.jfx.Beans.Implicits._
import org.junit.Assert
import javafx.beans.WeakInvalidationListener
import javafx.beans.property.SimpleIntegerProperty
import scala.ref.WeakReference
import scala.ref.ReferenceQueue
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import scala.collection.immutable.HashMap

class TestJfxLeaks {
  @Test
  def testPropertyListenerLeaks(): Unit = {
    var listener = new InvalidationListener {
      override def invalidated(observable: Observable): Unit = Unit
    }
    var weakListener = listener.weak

    val property = new SimpleIntegerProperty(0)
    property.addListener(weakListener)

    val refQueue1 = new ReferenceQueue[WeakInvalidationListener]
    val refQueue2 = new ReferenceQueue[WeakInvalidationListener]
    val ref1: Any = new WeakReference[InvalidationListener](listener, refQueue1)
    val ref2: Any = new WeakReference[InvalidationListener](weakListener, refQueue2)
    listener = null
    weakListener = null

    System.gc()
    Thread.sleep(1)

    // The invalidation listener is collected
    Assert.assertTrue(refQueue1.poll.nonEmpty)
    // But the weak invalidation listener still has a strong reference to it 
    Assert.assertTrue(refQueue2.poll.isEmpty)

    // At this point the weak invalidation gets an invalidation event and de-registers itself
    property.set(99)

    System.gc()
    Thread.sleep(1)

    Assert.assertTrue(refQueue1.poll.isEmpty)
    // So the weak invalidation listener can be collected.
    Assert.assertTrue(refQueue2.poll.nonEmpty)
  }

  @Test
  def testPropertyBindingLeaks(): Unit = {
    val property = new SimpleIntegerProperty(0)
    var binding = Bindings.greaterThan(1, property);

    val refQueue = new ReferenceQueue[BooleanBinding]
    val ref: Any = new WeakReference[BooleanBinding](binding, refQueue)
    binding = null

    System.gc()
    Thread.sleep(1)

    // Binding is collected, along with its associated invalidation listener,
    // but the weak invalidation listener reference is still strongly held
    // representing a small leak.
    Assert.assertTrue(refQueue.poll.nonEmpty)
  }

  @Test
  def testHotListenerDemo(): Unit = {
    var message: String = ""

    class HotInvalidationListener(val name: String) extends InvalidationListener {
      var properties = HashMap.empty[Any, Any]
 
      override def equals(o: Any): Boolean = o match {
        case that: HotInvalidationListener => {
          message = s"$name is stealing properties from ${that.name}"
          properties = that.properties
          true
        }
        case _ => false
      }

      override def hashCode(): Int = HotInvalidationListener.hashCode()

      override def invalidated(observable: Observable): Unit = Unit
    }

    object HotInvalidationListener

    val property = new SimpleIntegerProperty(0)
    val listener1 = new HotInvalidationListener("a")
    val listener2 = new HotInvalidationListener("b")
    property.addListener(listener1)
    listener1.properties += ("Hello" -> "World")
    property.removeListener(listener2)
    property.addListener(listener2)
    Assert.assertEquals("b is stealing properties from a", message)
    Assert.assertEquals(listener2.properties.get("Hello"), Some("World"))
  }
}
