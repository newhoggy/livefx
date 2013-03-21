package org.livefx

import org.junit.Test
import org.junit.Assert
import org.livefx.script.Change
import org.livefx.script.Update
import org.livefx.script.NoLo
import scala.reflect.macros.Context

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

  @Test
  def testStrongChangeSubscriber(): Unit = {
    var changes = List[Change[Int]]()
    val liveValue = new SimpleLiveValue[Int](0)
    
    var subscription = liveValue.changes.subscribe((_, change) => changes = change::changes)
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
    Assert.assertEquals(List(Update(NoLo, 12, 13), Update(NoLo, 11, 12), Update(NoLo, 0, 11)), changes)
  }
  
  @Test
  def testMap(): Unit = {
    val liveValue = new SimpleLiveValue[Int](0)
    val liveBinding = liveValue.map(_ * 2)
    liveValue.value = 1
    Assert.assertEquals(2, liveBinding.value)
    liveValue.value = 10
    Assert.assertEquals(20, liveBinding.value)
  }
  
  @Test
  def testForComprehension(): Unit = {
    val liveA = new SimpleLiveValue[Int](0)
    val liveB = new SimpleLiveValue[Int](0)
    val liveC = new SimpleLiveValue[Int](0)
    val liveZ = for {
      a <- liveA
      b <- liveB
      c <- liveC
    } yield a + b + c
    
    Assert.assertEquals(0, liveZ.value)
    liveB.value = 7
    Assert.assertEquals(7, liveZ.value)
    liveA.value = 3
    Assert.assertEquals(10, liveZ.value)
    liveC.value = 10
    Assert.assertEquals(20, liveZ.value)
    liveA.value = 1
    Assert.assertEquals(18, liveZ.value)
  }
  
  
  @Test
  def testForComprehensionShort(): Unit = {
    val liveA = new SimpleLiveValue[Short](0)
    val liveB = new SimpleLiveValue[Short](0)
    val liveC = new SimpleLiveValue[Short](0)
    val liveZ = for {
      a <- liveA
      b <- liveB
      c <- liveC
    } yield a + b + c
    
    Assert.assertEquals(0, liveZ.value)
    liveB.value = 7
    Assert.assertEquals(7, liveZ.value)
    liveA.value = 3
    Assert.assertEquals(10, liveZ.value)
    liveC.value = 10
    Assert.assertEquals(20, liveZ.value)
    liveA.value = 1
    Assert.assertEquals(18, liveZ.value)
  }
  
  @Test
  def testComparisons(): Unit = {
    import LiveNumeric.Implicits._
    
    val liveA = new SimpleLiveValue[Int](0)
    val liveB = new SimpleLiveValue[Int](0)
    val liveC = new SimpleLiveValue[Int](0)
    val liveZ = liveA + liveB + liveC

    Assert.assertEquals(0, liveZ.value)
    liveB.value = 7
    Assert.assertEquals(7, liveZ.value)
    liveA.value = 3
    Assert.assertEquals(10, liveZ.value)
    liveC.value = 10
    Assert.assertEquals(20, liveZ.value)
    liveA.value = 1
    Assert.assertEquals(18, liveZ.value)
  }

  @Test
  def testTraceSpoils(): Unit = {
    import LiveNumeric.Implicits._
    val liveA = new SimpleLiveValue[Int](1)
    val liveB = new SimpleLiveValue[Int](1)
    val liveC = traceSpoils(traceSpoils(liveA) + traceSpoils(liveB))
    var counter = 0
    liveC.spoils.subscribe { (_, spoilEvent) =>
      for (entry <- spoilEvent.trace) {
        counter += 1
      }
    }
    Assert.assertEquals(2, liveC.value)
    Assert.assertEquals(0, counter)
    liveA.value = 2
    Assert.assertEquals(3, liveC.value)
    Assert.assertEquals(2, counter)
    liveB.value = 3
    Assert.assertEquals(5, liveC.value)
    Assert.assertEquals(4, counter)
    liveA.value = 4
    Assert.assertEquals(7, liveC.value)
    Assert.assertEquals(6, counter)
    liveB.value = 5
    Assert.assertEquals(9, liveC.value)
    Assert.assertEquals(8, counter)
    liveA.value = 6
    liveB.value = 7
    Assert.assertEquals(13, liveC.value)
    Assert.assertEquals(10, counter)
  }
}
