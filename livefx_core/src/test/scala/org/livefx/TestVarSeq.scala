package org.livefx

import org.junit.Test
import org.junit.Assert
import org.livefx.trees.indexed.Tree

class TestVarSeq {
  @Test
  def testInsertUpdateRemove(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    val tree: VarSeq[Int] = new VarSeq[Int]()

    for (i <- 0 to 100) {
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          list.insert(index, i)
          tree.insert(index, i)
        case 1 =>
          if (list.size > 0) {
            val index = random.nextInt(list.size)
            list(index) = i
            tree(index) = i
          }
      }

      Assert.assertEquals(list.toList, Tree.iterator(tree.value).toList)
    }

    for (i <- 0 to list.size - 1) {
      val index = random.nextInt(list.size)
      list.remove(index)
      tree.remove(index)
      Assert.assertEquals(list.toList, Tree.iterator(tree.value).toList)
    }
  }
}
