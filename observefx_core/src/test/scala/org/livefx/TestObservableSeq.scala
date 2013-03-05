package org.livefx

import org.junit.Test
import org.livefx._
import org.junit.Assert

class TestObservableSeq {
  @Test
  def testLiveMap(): Unit = {
    val buffer = new ArrayBuffer[Int] with LiveBuffer[Int]
    val stringSeq = buffer.liveMap { value => "[" + value + "]" }
    buffer.insert(0, 0)
    Assert.assertTrue(stringSeq.sameElements(List("[0]")))
    buffer.append(1)
    Assert.assertTrue(stringSeq.sameElements(List("[0]", "[1]")))
    buffer.prepend(2)
    Assert.assertTrue(stringSeq.sameElements(List("[2]", "[0]", "[1]")))
    buffer.insertAll(1, List(3, 4, 5))
    Assert.assertTrue(stringSeq.sameElements(List("[2]", "[3]", "[4]", "[5]", "[0]", "[1]")))
    buffer.clear()
    Assert.assertTrue(stringSeq.sameElements(List()))
  }
  
  @Test
  def testLiveCounted(): Unit = {
    val buffer = new ArrayBuffer[Int] with LiveBuffer[Int]
    val counts = buffer.liveCounted
    buffer.insert(0, 0)
    Assert.assertTrue(counts == Map(0 -> 1))
    buffer.insert(0, 0)
    Assert.assertTrue(counts == Map(0 -> 2))
    buffer.insert(0, 0)
    Assert.assertTrue(counts == Map(0 -> 3))
    buffer(1) = 1
    Assert.assertTrue(counts == Map(1 -> 1, 0 -> 2))
  }

  @Test
  def testLiveHashed(): Unit = {
    val buffer = new ArrayBuffer[Int] with LiveBuffer[Int]
    val unique = buffer.liveSet
    buffer.insert(0, 3)
    Assert.assertTrue(unique == Set(3))
    buffer.insert(0, 4)
    Assert.assertTrue(unique == Set(3, 4))
    buffer.insert(1, 5)
    Assert.assertTrue(unique == Set(3, 4, 5))
    buffer.insert(1, 3)
    Assert.assertTrue(unique == Set(3, 4, 5))
    buffer.remove(1)
    Assert.assertTrue(unique == Set(3, 4, 5))
    buffer.remove(0)
    Assert.assertTrue(unique == Set(3, 5))
    buffer(1) = 1
  }
}
