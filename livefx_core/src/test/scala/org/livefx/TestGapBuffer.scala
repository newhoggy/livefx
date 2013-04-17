package org.livefx

import org.junit.Test
import org.junit.Assert
import org.livefx.gap.Branch
import org.livefx.gap.Config
import org.livefx.gap.Leaf
import org.livefx.gap.Root

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
    var buffer = new Root[Int](Config(4))
    
    buffer = buffer.insertL(1)
    Assert.assertEquals(Leaf(1, List(1), List(), 0), buffer.child)
    Assert.assertEquals(List(1), buffer.iterator.toList)
    buffer = buffer.insertL(2)
    Assert.assertEquals(Leaf(2, List(2, 1), List(), 0), buffer.child)
    Assert.assertEquals(List(1, 2), buffer.iterator.toList)
    buffer = buffer.insertL(3)
    Assert.assertEquals(Leaf(3, List(3, 2, 1), List(), 0), buffer.child)
    Assert.assertEquals(List(1, 2, 3), buffer.iterator.toList)
    buffer = buffer.insertR(4)
    Assert.assertEquals(Leaf(3, List(3, 2, 1), List(4), 1), buffer.child)
    Assert.assertEquals(List(1, 2, 3, 4), buffer.iterator.toList)
    buffer = buffer.insertR(5)
    Assert.assertEquals(
        Branch(
            3,
            List(Leaf(2, List(2, 1), List(3), 1)),
            Leaf(0, List(), List(5, 4), 2),
            List(),
            0),
        buffer.child)
    Assert.assertEquals(List(1, 2, 3, 5, 4), buffer.iterator.toList)
    buffer = buffer.insertR(6)
    Assert.assertEquals(List(1, 2, 3, 6, 5, 4), buffer.iterator.toList)
  }
  
  @Test
  def testInsertLotsOfValues3(): Unit = {
    var buffer = new Root[Int](Config(5))
    val random = new scala.util.Random(0)
    
    for (i <- 0 to 100) {
      buffer = if (random.nextBoolean) buffer.insertL(i) else buffer.insertR(i)

      Assert.assertEquals((0 to i).toList, buffer.iterator.toList.sorted)
    }
  }

  @Test
  def testInsertLotsOfValues4(): Unit = {
    var buffer = new Root[Int](Config(5))
    val random = new scala.util.Random(0)
    
    for (i <- 0 to 100) {
      buffer = if (random.nextBoolean) buffer.insertL(i) else buffer.insertR(i)
    }

    Assert.assertEquals((0 to 100).toList, buffer.iterator.toList.sorted)
    
    buffer = buffer.moveTo(buffer.size / 2)

    Assert.assertEquals((0 to 100).toList, buffer.iterator.toList.sorted)

//    while (buffer.size > 0) {
//      buffer = if (random.nextBoolean) buffer.removeL() else buffer.removeR()
//    }
  }
}
