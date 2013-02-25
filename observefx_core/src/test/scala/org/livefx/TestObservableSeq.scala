package org.livefx

import org.junit.Test
import scala.collection.mutable.ArrayBuffer
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
    println("--> " + counts)
    buffer.insert(0, 0)
    println("--> " + counts)
    buffer.insert(0, 0)
    println("--> " + counts)
    buffer(1) = 1
    println("--> " + counts)
  }
}
