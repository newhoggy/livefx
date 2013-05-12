package org.livefx

import org.junit.Test
import org.junit.Assert
import org.livefx.trees.indexed.Tree
import org.livefx.script.Change
import org.livefx.script.Include
import org.livefx.script.Index
import org.livefx.script.Update
import org.livefx.script.Remove
import scalaz.Monoid

class TestVarSeq {
  @Test
  def testInsertUpdateRemove(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    val tree: VarSeq[Int] = new VarSeq[Int]()
    var change: Option[Change[Int]] = None
    tree.changes.subscribe{(p, e) => change = Some(e)}

    for (i <- 0 to 100) {
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          list.insert(index, i)
          tree.insert(index, i)
          Assert.assertEquals(Some(Include(Index(index), i)), change)
        case 1 =>
          if (list.size > 0) {
            val index = random.nextInt(list.size)
            val oldValue = list(index)
            list(index) = i
            tree(index) = i
            Assert.assertEquals(Some(Update(Index(index), i, oldValue)), change)
          }
      }

      Assert.assertEquals(list.toList, Tree.iterator(tree.value).toList)
    }

    for (i <- 0 to list.size - 1) {
      val index = random.nextInt(list.size)
      val oldValue = list(index)
      list.remove(index)
      tree.remove(index)
      Assert.assertEquals(Some(Remove(Index(index), oldValue)), change)
      
      Assert.assertEquals(list.toList, Tree.iterator(tree.value).toList)
    }
  }

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
  def testSetValueEvents(): Unit = {
    val random = new scala.util.Random(0)
    var list: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    val tree: VarSeq[Int] = new VarSeq[Int]()
    var updateCount = 0
    val sum: LiveValue[Int] = tree.fold(new Monoid[Int] {
      override def zero: Int = 0
      override def append(a: Int, b: => Int): Int = a + b
    })
//    tree.changes.subscribe{(p, e) => println(s"--> e: $e")}
    sum.updates.subscribe{(p, e) => updateCount += 1; Assert.assertEquals(list.sum, sum.value)}

    for (i <- 0 to 100) {
      random.nextInt(2) match {
        case 0 =>
          val index = random.nextInt(list.size + 1)
          list.insert(index, i)
          tree.value = tree.value.insert(index, i)
          updateCount -= 1
        case 1 =>
          if (list.size > 0) {
            val index = random.nextInt(list.size)
            val oldValue = list(index)
            list(index) = i
            tree.value = tree.value.update(index, i)
            updateCount -= 1
          }
      }

      Assert.assertEquals(list.toList, Tree.iterator(tree.value).toList)
    }

    for (i <- 0 to list.size - 1) {
      val index = random.nextInt(list.size)
      val oldValue = list(index)
      list.remove(index)
      tree.value = tree.value.delete(index)
      updateCount -= 1
    }
    
    Assert.assertEquals(0, updateCount)
  }
}
