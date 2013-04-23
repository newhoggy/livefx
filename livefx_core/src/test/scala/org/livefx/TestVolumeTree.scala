package org.livefx

import org.livefx.volume.RedBlackTree
import org.junit.Test
import org.junit.Assert

class TestVolumeTree {
  @Test
  def test1(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    var tree: RedBlackTree.Tree[Int] = RedBlackTree.Leaf

    for (i <- 0 to 100) {
      random.nextInt(1) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          println(s"--> (${list.size})insert($index, $i)")
          list.insert(index, i)
          tree = RedBlackTree.insert(tree, index, i, false)
      }

      println(s"--> tree: $tree")
      Assert.assertEquals(list.toList, RedBlackTree.iterator(tree).toList)
    }
  }
}
