package org.livefx.util

import java.io.Closeable

import scala.ref.{ReferenceQueue, WeakReference}

abstract class TidyWeakReference[A <: AnyRef](a: A, queue: ReferenceQueue[A]) extends WeakReference[A](a, queue) with Closeable
