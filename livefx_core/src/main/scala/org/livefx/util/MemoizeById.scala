package org.livefx.util

class MemoizeById[A, K, B](fk: A => K)(f: A => B) extends (A => B) {
  private val map: WeakIdHashMap[K, B] = new WeakIdHashMap[K, B]
  
  def apply(a: A): B = fk(a) match { case k => map.getOrElseUpdate(k, f(a)) }
}

object MemoizeById {
  def byKey[A, K, B](fk: A => K)(f: A => B): A => B = new MemoizeById[A, K, B](fk)(f)

  def apply[A, B](f: A => B): A => B = new MemoizeById[A, A, B](identity)(f)
}
