package org.livefx

import org.junit.Test
import scalaz.Monoid
import org.junit.Assert

class TestLiveSeq {
  @Test
  def testFold(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    val tree: VarTreeSeq[Int] = new VarTreeSeq[Int]()
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
    val random = new scala.util.Random(0)
    val tree1: VarTreeSeq[Int] = new VarTreeSeq[Int]()
    val tree2 = tree1.asLiveTreeSeq.map(_ * 10)
    val sum1: LiveValue[Int] = tree1.fold(new Monoid[Int] {
      override def zero: Int = 0
      override def append(a: Int, b: => Int): Int = a + b
    })
    val sum2: LiveValue[Int] = tree2.fold(new Monoid[Int] {
      override def zero: Int = 0
      override def append(a: Int, b: => Int): Int = a + b
    })

    for (i <- 0 to 100) {
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(tree1.value.size + 1)
          tree1.insert(index, i)
          Assert.assertEquals(sum1.value * 10, sum2.value)
        case 1 =>
          if (tree1.value.size > 0) {
            val index = random.nextInt(tree1.size.value)
            val oldValue = tree1(index)
            tree1(index) = i
            Assert.assertEquals(sum1.value * 10, sum2.value)
          }
      }
    }

    for (i <- 0 to tree1.value.size - 1) {
      val index = random.nextInt(tree1.value.size)
      val oldValue = tree1(index)
      tree1.remove(index)
      Assert.assertEquals(sum1.value * 10, sum2.value)
    }
    
    Assert.assertEquals(sum1.value * 10, sum2.value)
  }
}
