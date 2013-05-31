package org.livefx.util

import scala.ref.ReferenceQueue
import scala.ref.WeakReference
import scala.ref.Reference

object TidyReferenceQueue extends ReferenceQueue[Nothing] {
  def tidy(): Unit = poll match {
    case Some(reference: TidyWeakReference[_]) => reference.dispose()
    case _ =>
  }

  final def weak[A <: AnyRef](a: A): WeakReference[A] = new WeakReference(a, this)
}
