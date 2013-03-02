package org.livefx

import org.junit.Test
import org.junit.Assert

class TestSimpleLiveValue {
  @Test
  def testGetAndSet(): Unit = {
    val liveValue = new SimpleLiveValue[Int](0)
    
    Assert.assertEquals(0, liveValue.value)
    Assert.assertEquals(false, liveValue.spoiled)
    liveValue.value = 1
    Assert.assertEquals(1, liveValue.value)
    Assert.assertEquals(false, liveValue.spoiled)
    
    liveValue.spoils.subscribe { (publisher, event) =>
      println("--> spoiled")
    }
    liveValue.value = 2
  }
}
