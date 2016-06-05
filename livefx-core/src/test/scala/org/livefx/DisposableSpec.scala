package org.livefx

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
  }
}
