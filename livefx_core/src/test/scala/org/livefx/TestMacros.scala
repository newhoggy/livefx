package org.livefx

import org.junit.Test

class TestMacros {
  @Test
  def testMacro(): Unit = {
    val x = org.livefx.macro.debug(1)
  }
}
