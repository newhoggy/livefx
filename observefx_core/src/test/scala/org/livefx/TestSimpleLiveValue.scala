package org.livefx

import org.junit.Test
import org.junit.Assert
import org.livefx.script.Change
import org.livefx.script.Update
import org.livefx.script.NoLo

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

  @Test
  def testChangeSubscriber(): Unit = {
    var changes = List[Change[Int]]()
    val liveValue = new SimpleLiveValue[Int](0)
    
    val subscription = liveValue.changes.subscribe((_, change) => changes = change::changes)
    Assert.assertEquals(0, liveValue.value)
    Assert.assertEquals(Nil, changes)

    liveValue.value = 11
    Assert.assertEquals(11, liveValue.value)
    Assert.assertEquals(List(Update(NoLo, 0, 11)), changes)

    liveValue.value = 12
    Assert.assertEquals(12, liveValue.value)
    Assert.assertEquals(List(Update(NoLo, 11, 12), Update(NoLo, 0, 11)), changes)
    
    subscription.dispose
    liveValue.value = 13
    Assert.assertEquals(13, liveValue.value)
    Assert.assertEquals(List(Update(NoLo, 11, 12), Update(NoLo, 0, 11)), changes)
  }

  @Test
  def testWeakChangeSubscriber(): Unit = {
    var changes = List[Change[Int]]()
    val liveValue = new SimpleLiveValue[Int](0)
    
    var subscription = liveValue.changes.subscribeWeak((_, change) => changes = change::changes)
    Assert.assertEquals(0, liveValue.value)
    Assert.assertEquals(Nil, changes)

    liveValue.value = 11
    Assert.assertEquals(11, liveValue.value)
    Assert.assertEquals(List(Update(NoLo, 0, 11)), changes)

    liveValue.value = 12
    Assert.assertEquals(12, liveValue.value)
    Assert.assertEquals(List(Update(NoLo, 11, 12), Update(NoLo, 0, 11)), changes)
    
    subscription = null
    System.gc()
    liveValue.value = 13
    Assert.assertEquals(13, liveValue.value)
    Assert.assertEquals(List(Update(NoLo, 11, 12), Update(NoLo, 0, 11)), changes)
  }
}
