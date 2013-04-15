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
    Assert.assertEquals(List(1, 2, 3, 5, 4), buffer.iterator.toList)
    buffer = buffer.insertR(6)
    Assert.assertEquals(List(1, 2, 3, 6, 5, 4), buffer.iterator.toList)
    println("--> load: " + buffer.branchLoad)
  }
  
  @Test
  def testInsertLotsOfValues3(): Unit = {
    var buffer = new GapRoot[Int](GapConfig(5))
    val random = new scala.util.Random(0)
    
    for (i <- 0 to 20) {
      println(s"--> $i")
      if (i == 9) {
//        Debug.debug = true
        // List(0, 1, 3, 4, 6, 8, 7, 5, 2), load: Map(1 -> 1)
//        println("--> problem: " + buffer.iterator.toList + ", load: " + buffer.branchLoad)
//        GapRoot(GapConfig(5),GapBranch(4,List(GapLeaf(2,List(1, 0),List(3, 4),2)),GapLeaf(2,List(8, 6),List(7, 5, 2),3),List(),0))
//        GapRoot(GapConfig(5),GapBranch(4,List(GapLeaf(2,List(8, 6),List(),0), GapLeaf(2,List(1, 0),List(3, 4),2)),GapLeaf(0,List(),List(7, 5, 2),3),List(),0))
//GapRoot(GapConfig(5),GapBranch(4,List(GapLeaf(2,List(8, 6),List(),0), GapLeaf(2,List(1, 0),List(3, 4),2)),GapLeaf(0,List(),List(7, 5, 2),3),List(),0))
      }

      random.nextBoolean match {
        case true => {
//          if (i == 9) println("--> insertL")
          buffer = buffer.insertL(i)
        }
        case false => {
          if (i == 9) println("--> insertR")
          buffer = buffer.insertR(i)
        }
      }

      if (i == 9 || i == 8 || i == 10 || i == 11) {
        println(s"--> problem($i): ${buffer.child.pretty(true)}")
      }
      println(s"--> problem($i): ${buffer.iterator.toList}, load: ${buffer.branchLoad}")
        // [4), [2), 1, 0, *, 3, 4, (sizeR], [2), 8, 6, *, 7, 5, 2, (sizeR], (0] 
        // [5), [2), 8, 6, *, (sizeR], [2), 1, 0, *, 3, 4, (sizeR], [0), *, 7, 5, 2, (sizeR], (0] 
        // [5), [2), 8, 6, *, (sizeR], [2), 1, 0, *, 3, 4, (sizeR], [0), *, 10, 7, 5, 2, (sizeR], (0] 
    }
  }
}
