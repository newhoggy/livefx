package org.livefx

trait LiveIntegral[T] extends LiveNumeric[T] {
  def quot(x: LiveValue[T], y: LiveValue[T]): LiveValue[T] = for (xv <- x; yv <- y) yield integral.quot(xv, yv)
  def rem(x: LiveValue[T], y: LiveValue[T]): LiveValue[T] = for (xv <- x; yv <- y) yield integral.rem(xv, yv)
  
  def integral: Integral[T]
  override def numeric: Numeric[T] = integral

  class IntegralOps(lhs: LiveValue[T]) extends Ops(lhs) {
    def /(rhs: LiveValue[T]) = quot(lhs, rhs)
    def %(rhs: LiveValue[T]) = rem(lhs, rhs)
    def /%(rhs: LiveValue[T]) = (quot(lhs, rhs), rem(lhs, rhs))
  }
  override implicit def mkNumericOps(lhs: LiveValue[T]): IntegralOps = new IntegralOps(lhs)
}

object Integral {
  trait ExtraImplicits {
    /** The regrettable design of Numeric/Integral/Fractional has them all
     *  bumping into one another when searching for this implicit, so they
     *  are exiled into their own companions.
     */
    implicit def infixIntegralOps[T](x: T)(implicit num: Integral[T]): Integral[T]#IntegralOps = new num.IntegralOps(x)
  }
  object Implicits extends ExtraImplicits
}
