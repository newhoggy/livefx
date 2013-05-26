package org.livefx.util

import java.lang.ref.WeakReference
import java.lang.ref.ReferenceQueue

class WeakIdRef[K](k: K, queue: ReferenceQueue[K]) extends WeakReference[K](k, queue) {
  override val hashCode: Int = System.identityHashCode(k)

  override def equals(any: Any): Boolean = {
    this == any || (any match {
      case that: WeakIdRef[K] => this.get == that.get
      case _ => false
    })
  }
}

object WeakIdRef {
  def apply[K](k: K)(implicit queue: ReferenceQueue[K]) = new WeakIdRef[K](k, queue)
}
