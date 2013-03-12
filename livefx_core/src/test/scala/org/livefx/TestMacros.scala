package org.livefx

import org.junit.Test

class TestMacros {
  @Test
  def testMacro(): Unit = {
    implicit val spoilTrace = List("hello")
    val x = org.livefx.macro.debug {
      implicit val spoilTrace = List("world")
      1
    }
  }
}
