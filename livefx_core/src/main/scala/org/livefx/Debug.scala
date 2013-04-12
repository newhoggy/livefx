package org.livefx

object Debug {
  var debug = false
  var level = 0
  
  def print(text: String): Unit = {
    if (debug) {
      val indent = "  " * level
      println(s"--> $indent$text")
    }
  }
  
  def trace[T](text: String)(f: => T): T = {
    if (debug) {
      val indent = "  " * level
      try {
        println(s"--> ${indent}enter: " + text)
        level += 1
        f
      } catch {
        case e: Exception => {
          println(s"--> ${indent}exception: $e")
          throw e
        }
      } finally {
        level -= 1
        println(s"--> ${indent}exit: " + text)
      }
    } else {
      f
    }
  }
}
