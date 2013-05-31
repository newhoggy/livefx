package org.livefx.util

import java.util.concurrent.atomic.AtomicBoolean
import scala.ref.ReferenceQueue
import scala.ref.WeakReference
import scala.ref.Reference

object TidyRefQueue {
  private val queue = new ReferenceQueue[Nothing]
  private val taken = new AtomicBoolean(false)

  private def poll(): Option[Reference[Nothing]] = {
    if (!taken.getAndSet(true)) {
      try {
        queue.poll
      } finally {
        taken.set(false)
      }
    } else {
      None
    }
  }

//  def tidy(): Unit = poll() match {
//    case Some(WeakReference(ref): Runnable) =>
//  }

  final def weak[A <: AnyRef](reference: A): WeakReference[A] = new WeakReference(reference, queue)
}
