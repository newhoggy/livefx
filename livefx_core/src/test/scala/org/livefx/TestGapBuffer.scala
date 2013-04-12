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
  def testInsertLotsOfValues2(): Unit = {
    var buffer = new GapRoot[Int](GapConfig(4))
    
    buffer = buffer.insertL(1)
    Assert.assertEquals(GapLeaf(1, List(1), List(), 0), buffer.child)
    Assert.assertEquals(List(1), buffer.iterator.toList)
    buffer = buffer.insertL(2)
    Assert.assertEquals(GapLeaf(2, List(2, 1), List(), 0), buffer.child)
    Assert.assertEquals(List(1, 2), buffer.iterator.toList)
    buffer = buffer.insertL(3)
    Assert.assertEquals(GapLeaf(3, List(3, 2, 1), List(), 0), buffer.child)
    Assert.assertEquals(List(1, 2, 3), buffer.iterator.toList)
    buffer = buffer.insertR(4)
    Assert.assertEquals(GapLeaf(3, List(3, 2, 1), List(4), 1), buffer.child)
    Assert.assertEquals(List(1, 2, 3, 4), buffer.iterator.toList)
    buffer = buffer.insertR(5)
    Assert.assertEquals(
        GapBranch(
            3,
            List(GapLeaf(2, List(2, 1), List(3), 1)),
            GapLeaf(0, List(), List(5, 4), 2),
            List(),
            0),
        buffer.child)

    Debug.debug = true
    Assert.assertEquals(List(1, 2, 3, 5, 4), buffer.iterator.toList)
//    buffer = buffer.insertR(6)
//    Assert.assertEquals(List(1, 2, 3, 6, 5, 4), buffer.iterator.toList)
//    println(buffer)
  }
}
