package org.livefx

import org.livefx.volume.VolumeTree
import org.junit.Test
import org.junit.Assert

class TestVolumeTree {
  @Test
  def test1(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    var tree: VolumeTree[Int] = VolumeTree.Leaf

    for (i <- 0 to 100) {
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          println(s"--> (${list.size})insert($index, $i)")
          list.insert(index, i)
          tree = VolumeTree.insert(tree, index, i)
        case 1 =>
          if (list.size > 0) {
            val index = random.nextInt(list.size)
            println(s"--> (${list.size})update($index, $i)")
            list(index) = i
            tree = VolumeTree.update(tree, index, i, true)
          }
      }

      println(s"--> tree: $tree")
      Assert.assertEquals(list.toList, VolumeTree.iterator(tree).toList)
    }

    for (i <- 0 to list.size - 1) {
      val index = random.nextInt(list.size)
      println(s"--> [$i] remove($index)")
      list.remove(index)
      tree = VolumeTree.delete(tree, index)
      println(s"--> tree: $tree")
      Assert.assertEquals(list.toList, VolumeTree.iterator(tree).toList)
    }

    Assert.assertEquals(list.slice(10, 15), VolumeTree.iterator(VolumeTree.range(tree, 10, 15)).toList)
  }
}
