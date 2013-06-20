package org.livefx.util

import scala.ref.WeakReference
import scala.ref.ReferenceQueue
import org.livefx.Disposable

abstract class TidyWeakReference[A <: AnyRef](a: A, queue: ReferenceQueue[A]) extends WeakReference[A](a, queue) with Disposable
