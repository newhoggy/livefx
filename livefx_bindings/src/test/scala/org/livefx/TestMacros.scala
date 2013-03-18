package org.livefx

import org.junit.Test
import LiveNumeric.Implicits.infixNumericOps

class TestMacros {
  @Test
  def testMacro(): Unit = {
    import LiveNumeric.Implicits._
    val liveA = new SimpleLiveValue[Int](1)
    val liveB = new SimpleLiveValue[Int](1)
    val liveC = Debug.debug(liveA + liveB)
    liveC.spoils.subscribe { (_, _) =>
      println("--> spoiled")
      for (entry <- spoilStack) {
        entry.printTo(System.out)
      }
    }
    println(liveC.value)
    liveA.value = 2
    println(liveC.value)
    liveB.value = 3
    println(liveC.value)
    liveA.value = 4
    println(liveC.value)
    liveB.value = 5
    println(liveC.value)
  }
}
