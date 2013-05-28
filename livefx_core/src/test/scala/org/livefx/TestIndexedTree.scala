package org.livefx

import org.livefx.trees._
import org.junit.Test
import org.junit.Assert
import org.junit.Ignore
import org.livefx.trees.indexed._
import scala.collection.mutable.ArrayBuffer

class TestIndexedTree {
  @Test
  def test1(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    var tree: Tree[Int] = Leaf

    for (i <- 0 to 100) {
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          list.insert(index, i)
          tree = tree.insert(index, i)
        case 1 =>
          if (list.size > 0) {
            val index = random.nextInt(list.size)
            list(index) = i
            tree = tree.update(index, i)
          }
      }

      Assert.assertEquals(list.toList, Tree.iterator(tree).toList)
    }

    for (i <- 0 to list.size - 1) {
      val index = random.nextInt(list.size)
      list.remove(index)
      tree = tree.delete(index)
      Assert.assertEquals(list.toList, Tree.iterator(tree).toList)
    }
  }
}
