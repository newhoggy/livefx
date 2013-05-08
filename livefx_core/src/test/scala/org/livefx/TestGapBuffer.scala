package org.livefx

import org.junit.Test
import org.junit.Assert
import org.livefx.volume._

class TestGapBuffer {
  @Test
  def test1(): Unit = {
    val random = new scala.util.Random(0)
    implicit val hm: HasMonoid[Int, Int] = new HasMonoid[Int, Int] {
      final override def monoidOf(value: Int): Int = value
      final override def append(a: Int, b: => Int): Int = a + b
      final override val zero: Int = 0
    }
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    var tree: Root[Int, Int] = Root[Int, Int]()

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

      Assert.assertEquals(list.toList, tree.toList(Nil))
    }

    for (i <- 0 to list.size - 1) {
      val index = random.nextInt(list.size)
      list.remove(index)
      tree = tree.remove(index)._2
      Assert.assertEquals(list.toList, tree.toList(Nil))
    }
  }
}
