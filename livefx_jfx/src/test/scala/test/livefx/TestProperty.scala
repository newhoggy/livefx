package test.livefx

import org.junit.Test
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.Property
import org.livefx.jfx.Beans.Implicits._
import org.junit.Assert
import javafx.beans.property.SimpleIntegerProperty

class TestProperty {
  @Test
  def testProperty(): Unit = {
    val property: Property[Int] = new SimpleObjectProperty[Int](0)
    property <== 1
    Assert.assertEquals(property.value, 1)
    property <== 1 + 1
    Assert.assertEquals(property.value, 2)
    property <== 3 min 4
    Assert.assertEquals(property.value, 3)
  }

  @Test
  def testIntegerProperty(): Unit = {
    val property = new SimpleIntegerProperty(0)
    property <== 1
    Assert.assertEquals(property.value, 1)
    property <== 1 + 1
    Assert.assertEquals(property.value, 2)
    property <== 3 min 4
    Assert.assertEquals(property.value, 3)
  }
  
  @Test
  def testLive(): Unit = {
    val propertyA = new SimpleIntegerProperty(0)
    val liveA1 = propertyA.live
    val liveA2 = propertyA.live
    Assert.assertTrue(liveA1 eq liveA2)
  }
}
