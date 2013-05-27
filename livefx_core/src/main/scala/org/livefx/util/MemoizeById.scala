package org.livefx.util

class MemoizeById[A, K, B](fk: A => K)(f: A => B) extends (A => B) {
  private val map: WeakIdHashMap[K, B] = new WeakIdHashMap[K, B]
  
  def apply(a: A): B = fk(a) match { case k => map.getOrElseUpdate(k, f(a)) }
}
