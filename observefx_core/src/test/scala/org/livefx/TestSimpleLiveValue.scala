package org.livefx

import org.junit.Test
import org.junit.Assert

class TestSimpleLiveValue {
  @Test
  def testGetAndSet(): Unit = {
    var spoilCount = 0
    val liveValue = new SimpleLiveValue[Int](0)
    
    Assert.assertEquals(0, liveValue.value)
    Assert.assertEquals(false, liveValue.spoiled)
    liveValue.value = 1
    Assert.assertEquals(1, liveValue.value)
    Assert.assertEquals(false, liveValue.spoiled)
    
    val subscription = liveValue.spoils.subscribe((_, _) => spoilCount += 1)
    Assert.assertEquals(0, spoilCount)
    liveValue.value = 2
    Assert.assertEquals(1, spoilCount)
    Assert.assertEquals(2, liveValue.value)
    Assert.assertEquals(false, liveValue.spoiled)

    subscription.dispose
    liveValue.value = 3
    Assert.assertEquals(1, spoilCount)
    Assert.assertEquals(3, liveValue.value)
    Assert.assertEquals(false, liveValue.spoiled)
  }
}
