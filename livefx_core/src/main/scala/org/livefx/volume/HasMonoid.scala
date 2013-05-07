package org.livefx.volume

import scalaz.Monoid

trait HasMonoid[-A, M] extends Monoid[M] {
  def monoidOf(value: A): M 
}

object HasMonoid {
  def apply[A, M](f: A => M, m: Monoid[M]) = new HasMonoid[A, M] {
    override def monoidOf(value: A): M = f(value)
    override def append(a: M, b: => M): M = m.append(a, b)
    override val zero: M = m.zero
  }
}
