package org.livefx.util

import java.lang.ref.WeakReference
import java.lang.ref.ReferenceQueue

class WeakIdentityReference[K](k: K, queue: ReferenceQueue[K]) extends WeakReference[K](k, queue) {
  override val hashCode: Int = System.identityHashCode(k)

  override def equals(any: Any): Boolean = {
    this == any || (any match {
      case that: WeakIdentityReference[K] => this.get == that.get
      case _ => false
    })
  }
}

object WeakIdentityReference {
  def apply[K](k: K)(implicit queue: ReferenceQueue[K]) = new WeakIdentityReference[K](k, queue)
}
