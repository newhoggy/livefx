package org.livefx.util

import scala.ref.WeakReference
import scala.ref.ReferenceQueue

abstract class TidyWeakReference[A <: AnyRef](a: A, queue: ReferenceQueue[A]) extends WeakReference[A](a, queue) {
  def dispose(): Unit
}
