package org.livefx

import org.junit.Test
import org.junit.Assert

class TestGapBuffer {
  @Test
  def testInsertLeftValues(): Unit = {
    val buffer = new GapBuffer[Int]()
    
    buffer.insertL(1)
    buffer.insertL(2)
    buffer.insertL(3)
  }
}
