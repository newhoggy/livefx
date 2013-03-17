package org.livefx

import org.junit.Test
import LiveNumeric.Implicits.infixNumericOps

class TestMacros {
  @Test
  def testMacro(): Unit = {
    import LiveNumeric.Implicits._
    val liveA = new SimpleLiveValue[Int](1)
    val liveB = new SimpleLiveValue[Int](1)
    implicit val spoilTrace = List("hello")
    val y = debug {
      liveA + liveB
    }
  }
}
