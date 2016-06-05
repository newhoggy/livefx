package org.livefx.value

import scala.language.implicitConversions

object LiveNumeric {
  trait ExtraImplicits {
    /** These implicits create conversions from a value for which an implicit Numeric
     *  exists to the inner class which creates infix operations.  Once imported, you
     *  can write methods as follows:
     *  {{{
     *  def plus[T: Numeric](x: T, y: T) = x + y
     *  }}}
     */
    implicit def infixNumericOps[T](x: Live[T])(implicit num: LiveNumeric[T]): LiveNumeric[T]#Ops = new num.Ops(x)
  }
  object Implicits extends ExtraImplicits { }
  
  trait LiveIntIsIntegral extends LiveIntegral[Int] {
    def integral: Integral[Int] = Numeric.IntIsIntegral
  }
  implicit object IntIsIntegral extends LiveIntIsIntegral
}

trait LiveNumeric[T] extends LiveOrdering[T] { outer =>
  def plus(x: Live[T], y: Live[T]): Live[T] = for (xv <- x; yv <- y) yield numeric.plus(xv, yv)
  def minus(x: Live[T], y: Live[T]): Live[T] = for (xv <- x; yv <- y) yield numeric.minus(xv, yv)
  def times(x: Live[T], y: Live[T]): Live[T] = for (xv <- x; yv <- y) yield numeric.times(xv, yv)
  def negate(x: Live[T]): Live[T] = for (xv <- x) yield numeric.negate(xv)
  def fromInt(x: Live[Int]): Live[T] = for (xv <- x) yield numeric.fromInt(xv)
  def toInt(x: Live[T]): Live[Int] = for (xv <- x) yield numeric.toInt(xv)
  def toLong(x: Live[T]): Live[Long] = for (xv <- x) yield numeric.toLong(xv)
  def toFloat(x: Live[T]): Live[Float] = for (xv <- x) yield numeric.toFloat(xv)
  def toDouble(x: Live[T]): Live[Double] = for (xv <- x) yield numeric.toDouble(xv)

  def numeric: Numeric[T]
  override def ordering: Ordering[T] = numeric 
  
  // TODO: Find another way to do this than instantiate a new simple live value every time.
  def zero = fromInt(Var[Int](0))
  def one = fromInt(Var[Int](1))

  def abs(x: Live[T]): Live[T] = x.map(numeric.abs(_))
  def signum(x: Live[T]): Live[Int] = x.map(numeric.signum(_))

  class Ops(lhs: Live[T]) {
    def +(rhs: Live[T]) = plus(lhs, rhs)
    def -(rhs: Live[T]) = minus(lhs, rhs)
    def *(rhs: Live[T]) = times(lhs, rhs)
    def unary_-() = negate(lhs)
    def abs(): Live[T] = outer.abs(lhs)
    def signum(): Live[Int] = outer.signum(lhs)
    def toInt(): Live[Int] = outer.toInt(lhs)
    def toLong(): Live[Long] = outer.toLong(lhs)
    def toFloat(): Live[Float] = outer.toFloat(lhs)
    def toDouble(): Live[Double] = outer.toDouble(lhs)
  }

  implicit def mkNumericOps(lhs: Live[T]): Ops = new Ops(lhs)
}
