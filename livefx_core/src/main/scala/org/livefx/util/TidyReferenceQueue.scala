package org.livefx.util

import scala.ref.ReferenceQueue
import scala.ref.WeakReference
import scala.ref.Reference
import scala.annotation.tailrec

object TidyReferenceQueue extends ReferenceQueue[Nothing] {
  @tailrec
  def tidy(count: Int = 10): Unit = poll match {
    case Some(reference: TidyWeakReference[_]) =>
      reference.dispose()
      if (count >= 0) tidy(count - 1)
    case _ =>
  }
}
