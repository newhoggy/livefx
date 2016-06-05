package org.livefx.disposal

import org.livefx.OnDispose
import org.livefx.syntax.std.autoCloseable._
import org.specs2.mutable.Specification

class DisposableSpec extends Specification {
  "Disposable" >> {
    "when composed" should {
      "dispose in reverse order" >> {
        var value = 1
        val disposable1 = OnDispose(value += 1)
        val disposable2 = OnDispose(value *= 10)

        (disposable1 ++ disposable2).dispose()

        value ==== 11
      }
    }

    "dispose in reverse order in nested for comprehension" >> {
      var x = 1

      for (a <- OnDispose(x += 1)) {
        for (b <- OnDispose(x *= 10)) {
          "Hello world"
        }
      }: String

      x ==== 11
    }

    "dispose in reverse order in shared for comprehension" >> {
      var x = 1

      for (a <- OnDispose(x += 1); b <- OnDispose(x *= 10)) {
        "Hello World"
      }: String

      x ==== 11
    }
  }
}
