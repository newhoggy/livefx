//package org.livefx.value
//
//import scala.language.implicitConversions
//
//trait LiveOrdering[T] extends LivePartialOrdering[T] with Serializable {
//  outer =>
//
//  /** Returns whether a comparison between `x` and `y` is defined, and if so
//    * the result of `compare(x, y)`.
//    */
//  def tryCompare(x: Live[T], y: Live[T]) = compare(x, y).map(Some(_))
//
//  def ordering: Ordering[T]
//
//  /** Returns an integer whose sign communicates how x compares to y.
//   *
//   * The result sign has the following meaning:
//   *
//   *  - negative if x < y
//   *  - positive if x > y
//   *  - zero otherwise (if x == y)
//   */
//  def compare(x: Live[T], y: Live[T]): Live[Int] = for (xv <- x; yv <- y) yield ordering.compare(xv, yv)
//
//  /** Return true if `x` <= `y` in the ordering. */
//  override def lteq(x: Live[T], y: Live[T]): Live[Boolean] = compare(x, y).map(_ <= 0)
//
//  /** Return true if `x` >= `y` in the ordering. */
//  override def gteq(x: Live[T], y: Live[T]): Live[Boolean] = compare(x, y).map(_ >= 0)
//
//  /** Return true if `x` < `y` in the ordering. */
//  override def lt(x: Live[T], y: Live[T]): Live[Boolean] = compare(x, y).map(_ < 0)
//
//  /** Return true if `x` > `y` in the ordering. */
//  override def gt(x: Live[T], y: Live[T]): Live[Boolean] = compare(x, y).map(_ > 0)
//
//  /** Return true if `x` == `y` in the ordering. */
//  override def equiv(x: Live[T], y: Live[T]): Live[Boolean] = compare(x, y).map(_ == 0)
//
//  /** Return `x` if `x` >= `y`, otherwise `y`. */
//  def max(x: Live[T], y: Live[T]): Live[T] = for (xv <- x; yv <- y) yield if (ordering.compare(xv, yv) >= 0) xv else yv
//
//  /** Return `x` if `x` <= `y`, otherwise `y`. */
//  def min(x: Live[T], y: Live[T]): Live[T] = for (xv <- x; yv <- y) yield if (ordering.compare(xv, yv) <= 0) xv else yv
//
//  /** Return the opposite ordering of this one. */
//  override def reverse: LiveOrdering[T] = new LiveOrdering[T] {
//    override def reverse = outer
//    override def ordering = outer.ordering.reverse
//  }
//
//  def on[U](f: U => T): LiveOrdering[U] = new LiveOrdering[U] {
//    def ordering = outer.ordering.on(f)
//  }
//
//  /** This inner class defines comparison operators available for `T`. */
//  class Ops(lhs: Live[T]) {
//    def <(rhs: Live[T]) = lt(lhs, rhs)
//    def <=(rhs: Live[T]) = lteq(lhs, rhs)
//    def >(rhs: Live[T]) = gt(lhs, rhs)
//    def >=(rhs: Live[T]) = gteq(lhs, rhs)
//    def equiv(rhs: Live[T]) = LiveOrdering.this.equiv(lhs, rhs)
//    def max(rhs: Live[T]): Live[T] = LiveOrdering.this.max(lhs, rhs)
//    def min(rhs: Live[T]): Live[T] = LiveOrdering.this.min(lhs, rhs)
//  }
//
//  /** This implicit method augments `T` with the comparison operators defined
//    * in `scala.math.Ordering.Ops`.
//    */
//  implicit def mkOrderingOps(lhs: Live[T]): Ops = new Ops(lhs)
//}
