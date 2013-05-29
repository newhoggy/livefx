package test.livefx

import org.junit.Test
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.Property
import org.livefx.jfx.Observables.Implicits._
import org.junit.Assert

class TestProperty {
  @Test
  def testProperty(): Unit = {
    val property: Property[Int] = new SimpleObjectProperty[Int](0)
    property <== 1
    Assert.assertEquals(property.value, 1)
    
  }
}
