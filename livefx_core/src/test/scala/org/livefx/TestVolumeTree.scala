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
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          println(s"--> (${list.size})insert($index, $i)")
          list.insert(index, i)
          tree = RedBlackTree.insert(tree, index, i, false)
        case 1 =>
          if (list.size > 0) {
            val index = random.nextInt(list.size)
            println(s"--> (${list.size})update($index, $i)")
            list(index) = i
            tree = RedBlackTree.update(tree, index, i, true)
          }
      }

      println(s"--> tree: $tree")
      Assert.assertEquals(list.toList, RedBlackTree.iterator(tree).toList)
    }
  }
}
