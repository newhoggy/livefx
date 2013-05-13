package org.livefx

import org.junit.Test
import scalaz.Monoid
import org.junit.Assert

class TestLiveSeq {
  @Test
  def testFold(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    val tree: VarSeq[Int] = new VarSeq[Int]()
    var updateCount = 0
    val sum: LiveValue[Int] = tree.fold(new Monoid[Int] {
      override def zero: Int = 0
      override def append(a: Int, b: => Int): Int = a + b
    })
    sum.updates.subscribe{(p, e) => updateCount += 1; Assert.assertEquals(list.sum, sum.value)}

    for (i <- 0 to 100) {
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          list.insert(index, i)
          tree.insert(index, i)
          updateCount -= 1
        case 1 =>
          if (list.size > 0) {
            val index = random.nextInt(list.size)
            val oldValue = list(index)
            list(index) = i
            tree(index) = i
            updateCount -= 1
          }
      }
    }

    for (i <- 0 to list.size - 1) {
      val index = random.nextInt(list.size)
      val oldValue = list(index)
      list.remove(index)
      tree.remove(index)
      updateCount -= 1
    }
    
    Assert.assertEquals(0, updateCount)
  }

  @Test
  def testMap(): Unit = {
    val seqA = new VarSeq[Int]
//    val seqB = seqA.map(_ + 1)
//    val list = List(1)
//    list.map(_ + 1)
  }
}
