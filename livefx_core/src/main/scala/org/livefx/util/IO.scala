package org.livefx.util

import scala.util.DynamicVariable
import java.io.PrintStream
import java.io.OutputStream

object IO {
  val originalOut = System.out
  val suppressed = new DynamicVariable[Boolean](false)
  val switchedOut = new PrintStream(new OutputStream() {
    override def write(b: Int): Unit = {
      if (!suppressed.value) {
        originalOut.write(b)
      }
    }
  })
  System.setOut(switchedOut)
  def suppressStdOut[A](f: => A): A = {
    suppressed.withValue(true)(f)
  }
}
