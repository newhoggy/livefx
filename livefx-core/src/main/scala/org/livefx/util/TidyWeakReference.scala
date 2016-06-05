package org.livefx.util

import java.io.Closeable

import org.livefx.disposal.Disposable

import scala.ref.WeakReference
import scala.ref.ReferenceQueue

abstract class TidyWeakReference[A <: AnyRef](a: A, queue: ReferenceQueue[A]) extends WeakReference[A](a, queue) with Closeable
