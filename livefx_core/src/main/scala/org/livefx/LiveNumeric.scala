package org.livefx

object LiveNumeric {
  trait ExtraImplicits {
    /** These implicits create conversions from a value for which an implicit Numeric
     *  exists to the inner class which creates infix operations.  Once imported, you
     *  can write methods as follows:
     *  {{{
     *  def plus[T: Numeric](x: T, y: T) = x + y
     *  }}}
     */
    implicit def infixNumericOps[T](x: LiveValue[T])(implicit num: LiveNumeric[T]): LiveNumeric[T]#Ops = new num.Ops(x)
  }
  object Implicits extends ExtraImplicits { }
  
  trait LiveIntIsIntegral extends LiveIntegral[Int] {
    def integral: Integral[Int] = Numeric.IntIsIntegral
  }
  implicit object IntIsIntegral extends LiveIntIsIntegral
}

trait LiveNumeric[T] extends LiveOrdering[T] { outer =>
  def plus(x: LiveValue[T], y: LiveValue[T]): LiveValue[T] = for (xv <- x; yv <- y) yield numeric.plus(xv, yv)
  def minus(x: LiveValue[T], y: LiveValue[T]): LiveValue[T] = for (xv <- x; yv <- y) yield numeric.minus(xv, yv)
  def times(x: LiveValue[T], y: LiveValue[T]): LiveValue[T] = for (xv <- x; yv <- y) yield numeric.times(xv, yv)
  def negate(x: LiveValue[T]): LiveValue[T] = for (xv <- x) yield numeric.negate(xv)
  def fromInt(x: LiveValue[Int]): LiveValue[T] = for (xv <- x) yield numeric.fromInt(xv)
  def toInt(x: LiveValue[T]): LiveValue[Int] = for (xv <- x) yield numeric.toInt(xv)
  def toLong(x: LiveValue[T]): LiveValue[Long] = for (xv <- x) yield numeric.toLong(xv)
  def toFloat(x: LiveValue[T]): LiveValue[Float] = for (xv <- x) yield numeric.toFloat(xv)
  def toDouble(x: LiveValue[T]): LiveValue[Double] = for (xv <- x) yield numeric.toDouble(xv)

  def numeric: Numeric[T]
  override def ordering: Ordering[T] = numeric 
  
  // TODO: Find another way to do this than instantiate a new simple live value every time.
  def zero = fromInt(new Var[Int](0))
  def one = fromInt(new Var[Int](1))

  def abs(x: LiveValue[T]): LiveValue[T] = x.map(numeric.abs(_))
  def signum(x: LiveValue[T]): LiveValue[Int] = x.map(numeric.signum(_))

  class Ops(lhs: LiveValue[T]) {
    def +(rhs: LiveValue[T]) = plus(lhs, rhs)
    def -(rhs: LiveValue[T]) = minus(lhs, rhs)
    def *(rhs: LiveValue[T]) = times(lhs, rhs)
    def unary_-() = negate(lhs)
    def abs(): LiveValue[T] = outer.abs(lhs)
    def signum(): LiveValue[Int] = outer.signum(lhs)
    def toInt(): LiveValue[Int] = outer.toInt(lhs)
    def toLong(): LiveValue[Long] = outer.toLong(lhs)
    def toFloat(): LiveValue[Float] = outer.toFloat(lhs)
    def toDouble(): LiveValue[Double] = outer.toDouble(lhs)
  }

  implicit def mkNumericOps(lhs: LiveValue[T]): Ops = new Ops(lhs)
}
