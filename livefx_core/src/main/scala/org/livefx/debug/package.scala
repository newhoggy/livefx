package org.livefx

package object debug {
  implicit class Contract[T](result: T) {
    def precondition(requirement: => Boolean): T = {
      assert(requirement)
      result
    }

    def trace(f: T => Unit): T = {
      f(result)
      result
    }

    def postcondition(requirement: T => Boolean): T = {
      val pass = requirement(result)
      if (!pass) {
        println(s"postcondition failed for $result")
      }
      assert(pass, s"postcondition failed for $result")
      result
    }
  }
}
