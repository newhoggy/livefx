package org.livefx.util

import scala.collection.mutable.WeakHashMap

class Memoize[A, K, B](fk: A => K)(f: A => B) extends (A => B) {
  private val map: WeakHashMap[K, B] = new WeakHashMap[K, B]
  
  def apply(a: A): B = fk(a) match { case k => map.getOrElseUpdate(k, f(a)) }
}

object Memoize {
  def objectId(a: Any): Any = new AnyRef {
    override def hashCode(): Int = System.identityHashCode(a)
  }

  def apply[A, K, B](fk: A => K)(f: A => B): A => B = new Memoize[A, K, B](fk)(f)
}
