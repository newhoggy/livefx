package org.livefx.util

import scala.ref.ReferenceQueue
import scala.ref.WeakReference
import scala.ref.Reference
import scala.annotation.tailrec
import scala.concurrent._
import ExecutionContext.Implicits.global

object TidyReferenceQueue extends ReferenceQueue[Nothing] {
  @tailrec
  def tidy(count: Int = 10): Unit = poll match {
    case Some(reference: TidyWeakReference[_]) =>
      reference.dispose()
      if (count >= 0) tidy(count - 1)
    case _ =>
  }
}
