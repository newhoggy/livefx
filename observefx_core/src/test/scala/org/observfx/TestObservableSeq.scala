package org.observfx

import org.junit.Test
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ObservableBuffer
import org.observefx._

class TestObservableSeq {
  @Test
  def testMe(): Unit = {
    val buffer = new ArrayBuffer[Int] with ObservableBuffer[Int]
    val stringSeq = buffer.liveMap { value => "[" + value + "]" }
    buffer.insert(0, 0)
    println(stringSeq)
    buffer.append(1)
    println(stringSeq)
    buffer.prepend(2)
    println(stringSeq)
    buffer.insertAll(1, List(3, 4, 5))
    println(stringSeq)
  }
}