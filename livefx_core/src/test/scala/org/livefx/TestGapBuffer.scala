package org.livefx

import org.junit.Test
import org.junit.Assert

class TestGapBuffer {
  @Test
  def testHasAssert(): Unit = {
    val hasAssert = try {
      assert(false)
      false
    } catch {
      case e: AssertionError => true
    }
    
    Assert.assertTrue(hasAssert)
  }
  
  @Test
  def testInsertLeftValues(): Unit = {
    val buffer = new GapBuffer[Int]()
    
    buffer.insertL(1)
    Assert.assertEquals(List(1), buffer.iterator.toList)
    buffer.insertL(2)
    Assert.assertEquals(List(1, 2), buffer.iterator.toList)
    buffer.insertL(3)
    Assert.assertEquals(List(1, 2, 3), buffer.iterator.toList)
    buffer.insertR(4)
    Assert.assertEquals(List(1, 2, 3, 4), buffer.iterator.toList)
    buffer.insertR(5)
    Assert.assertEquals(List(1, 2, 3, 5, 4), buffer.iterator.toList)
    buffer.insertR(6)
    Assert.assertEquals(List(1, 2, 3, 6, 5, 4), buffer.iterator.toList)
  }
}
