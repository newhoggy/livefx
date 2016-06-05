package org.livefx.util

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.ref.ReferenceQueue

object TidyReferenceQueue extends ReferenceQueue[Nothing] {
  @tailrec
  def tidy(count: Int = 10): Unit = poll match {
    case Some(reference: TidyWeakReference[_]) =>
      reference.dispose()
      if (count >= 0) tidy(count - 1)
    case _ =>
  }
}