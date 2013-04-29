package org.livefx

import org.junit.Test
import org.junit.Assert
import org.livefx.gap._

class TestGapBuffer {
  @Test
  def test1(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    var tree: Root[Int] = Root[Int]()

    for (i <- 0 to 100) {
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          println(s"--> (${list.size})insert($index, $i)")
          list.insert(index, i)
          tree = tree.insert(index, i)
        case 1 =>
          if (list.size > 0) {
            val index = random.nextInt(list.size)
            println(s"--> ($list.update($index, $i)")
            list(index) = i
            tree = tree.update(index, i)
          }
      }

      println(s"--> tree: $tree, ${tree.toList(Nil)}, $list")
      Assert.assertEquals(list.toList, tree.toList(Nil))
    }

//    for (i <- 0 to list.size - 1) {
//      val index = random.nextInt(list.size)
//      println(s"--> [$i] remove($index)")
//      list.remove(index)
//      tree = tree.delete(index)
//      println(s"--> tree: $tree")
//      Assert.assertEquals(list.toList, Tree.iterator(tree).toList)
//    }
  }
}
