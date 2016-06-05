package org.livefx.core.disposal.syntax.std

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec

package object atomicReference {
  implicit class AtomicReferenceOps_YYKh2cf[A](val self: AtomicReference[A]) extends AnyVal {
    def update(f: A => A): A = {
      @tailrec
      def go(oldValue: A): A = {
        val currentValue = self.getAndSet(f(oldValue))

        if (currentValue != oldValue) {
          go(currentValue)
        } else {
          currentValue
        }
      }

      go(self.get())
    }
  }
}
