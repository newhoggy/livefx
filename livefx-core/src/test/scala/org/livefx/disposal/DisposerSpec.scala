package org.livefx.disposal

import org.livefx.OnDispose
import org.specs2.mutable.Specification

class DisposerSpec extends Specification {
  "Disposer" should {
    "dispose in reverse order" >> {
      var value = 1
      val disposer = new Disposer()

      disposer.disposes(OnDispose(value += 1))
      disposer.disposes(OnDispose(value *= 10))
      disposer.dispose()

      value ==== 11
    }
  }
}
