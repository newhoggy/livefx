package org.livefx

import org.junit.Test

class TestMacros {
  import org.livefx._
  
  @Test
  def testMacro(): Unit = {
    import LiveNumeric.Implicits._
    val liveA = new SimpleLiveValue[Int](1)
    val liveB = new SimpleLiveValue[Int](1)
    implicit val spoilTrace = List("hello")
    val x = org.livefx.macro.debug {
      org.livefx.bindTrace(bindTrace(liveA) + bindTrace(liveB))
    }
    val y = org.livefx.macro.debug {
      liveA + liveB
    }
  }
}
