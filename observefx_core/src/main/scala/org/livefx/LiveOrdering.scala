package org.livefx

trait LiveOrdering[T] extends LivePartialOrdering[T] with Serializable {
  outer =>

  /** Returns whether a comparison between `x` and `y` is defined, and if so
    * the result of `compare(x, y)`.
    */
  def tryCompare(x: LiveValue[T], y: LiveValue[T]) = compare(x, y).map(Some(_))

  def ordering: Ordering[T]
  
  /** Returns an integer whose sign communicates how x compares to y.
   *
   * The result sign has the following meaning:
   *
   *  - negative if x < y
   *  - positive if x > y
   *  - zero otherwise (if x == y)
   */
  def compare(x: LiveValue[T], y: LiveValue[T]): LiveValue[Int] = for (xv <- x; yv <- y) yield ordering.compare(xv, yv)

  /** Return true if `x` <= `y` in the ordering. */
  override def lteq(x: LiveValue[T], y: LiveValue[T]): LiveValue[Boolean] = compare(x, y).map(_ <= 0)

  /** Return true if `x` >= `y` in the ordering. */
  override def gteq(x: LiveValue[T], y: LiveValue[T]): LiveValue[Boolean] = compare(x, y).map(_ >= 0)

  /** Return true if `x` < `y` in the ordering. */
  override def lt(x: LiveValue[T], y: LiveValue[T]): LiveValue[Boolean] = compare(x, y).map(_ < 0)

  /** Return true if `x` > `y` in the ordering. */
  override def gt(x: LiveValue[T], y: LiveValue[T]): LiveValue[Boolean] = compare(x, y).map(_ > 0)

  /** Return true if `x` == `y` in the ordering. */
  override def equiv(x: LiveValue[T], y: LiveValue[T]): LiveValue[Boolean] = compare(x, y).map(_ == 0)

  /** Return `x` if `x` >= `y`, otherwise `y`. */
  def max(x: LiveValue[T], y: LiveValue[T]): LiveValue[T] = for (xv <- x; yv <- y) yield if (ordering.compare(xv, yv) >= 0) xv else yv 

  /** Return `x` if `x` <= `y`, otherwise `y`. */
  def min(x: LiveValue[T], y: LiveValue[T]): LiveValue[T] = for (xv <- x; yv <- y) yield if (ordering.compare(xv, yv) <= 0) xv else yv

  /** Return the opposite ordering of this one. */
  override def reverse: LiveOrdering[T] = new LiveOrdering[T] {
    override def reverse = outer
    override def ordering = outer.ordering.reverse
  }

  def on[U](f: U => T): LiveOrdering[U] = new LiveOrdering[U] {
    def ordering = outer.ordering.on(f)
  }

  /** This inner class defines comparison operators available for `T`. */
  class Ops(lhs: LiveValue[T]) {
    def <(rhs: LiveValue[T]) = lt(lhs, rhs)
    def <=(rhs: LiveValue[T]) = lteq(lhs, rhs)
    def >(rhs: LiveValue[T]) = gt(lhs, rhs)
    def >=(rhs: LiveValue[T]) = gteq(lhs, rhs)
    def equiv(rhs: LiveValue[T]) = LiveOrdering.this.equiv(lhs, rhs)
    def max(rhs: LiveValue[T]): LiveValue[T] = LiveOrdering.this.max(lhs, rhs)
    def min(rhs: LiveValue[T]): LiveValue[T] = LiveOrdering.this.min(lhs, rhs)
  }

  /** This implicit method augments `T` with the comparison operators defined
    * in `scala.math.Ordering.Ops`.
    */
  implicit def mkOrderingOps(lhs: LiveValue[T]): Ops = new Ops(lhs)
}
