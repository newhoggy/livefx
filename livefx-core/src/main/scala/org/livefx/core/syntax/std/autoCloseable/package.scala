package org.livefx.core.syntax.std

package object autoCloseable {
  implicit class AutoCloseableOps_YYKh2cf[A <: AutoCloseable](val self: A) extends AnyVal {
    final def foreach[B](f: A => B): B = {
      try {
        f(self)
      } finally {
        try {
          self.close()
        } catch {
          case e: Exception =>
        }
      }
    }
  }
}
