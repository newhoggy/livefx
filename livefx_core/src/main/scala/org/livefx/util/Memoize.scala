package org.livefx.util

import scala.collection.mutable.WeakHashMap

class Memoize[A, K, B](fk: A => K)(f: K => B) extends (A => B) {
  private val map: WeakHashMap[K, B] = new WeakHashMap[K, B]
  
  def apply(a: A): B = fk(a) match { case k => map.getOrElseUpdate(k, f(k)) }
}
