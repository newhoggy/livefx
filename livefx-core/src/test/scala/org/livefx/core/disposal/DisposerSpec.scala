package org.livefx.core.disposal

import org.livefx.core.std.autoCloseable._
import org.livefx.core.syntax.disposable._
import org.specs2.mutable.Specification

class DisposerSpec extends Specification {
  "Disposer" should {
    "dispose in reverse order" >> {
      var value = 1
      val disposer = new Disposer()

      disposer.disposes(OnClose(value += 1))
      disposer.disposes(OnClose(value *= 10))
      disposer.dispose()

      value ==== 11
    }
  }
}
