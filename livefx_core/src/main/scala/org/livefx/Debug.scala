package org.livefx

object Debug {
  final def v[T](value: T): T = value

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
      val result = try {
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
      }
      println(s"--> ${indent}exit: " + text + ", " + result)
      result
    } else {
      f
    }
  }

  def trace[A, T](text: String, a: A)(f: => T): T = {
    if (debug) {
      val indent = "  " * level
      val result = try {
        println(s"--> ${indent}enter: " + text + ", " + a)
        level += 1
        f
      } catch {
        case e: Exception => {
          println(s"--> ${indent}exception: $e")
          throw e
        }
      } finally {
        level -= 1
      }
      println(s"--> ${indent}exit: " + text + ", " + result)
      result
    } else {
      f
    }
  }
}
