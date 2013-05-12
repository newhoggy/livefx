package org.livefx.util

import scala.util.DynamicVariable

class NoRecurse {
  private val called = new DynamicVariable[Boolean](false)
  
  final def apply[A](f: => A): Option[A] = if (called.value) Some(called.withValue(true)(f)) else None
  
  final def apply(f: => Unit): Unit = if (called.value) called.withValue(true)(f)
}
