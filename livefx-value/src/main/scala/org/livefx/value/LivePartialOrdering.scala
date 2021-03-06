//package org.livefx.value
//
//trait LivePartialOrdering[T] extends LiveEquiv[T] { outer =>
//  /** Result of comparing `x` with operand `y`.
//   *  Returns `None` if operands are not comparable.
//   *  If operands are comparable, returns `Some(r)` where
//   *  - `r < 0`    iff    `x < y`
//   *  - `r == 0`   iff    `x == y`
//   *  - `r > 0`    iff    `x > y`
//   */
//  def tryCompare(x: Live[T], y: Live[T]): Live[Option[Int]]
//
//  /** Returns `'''true'''` iff `x` comes before `y` in the ordering.
//   */
//  def lteq(x: Live[T], y: Live[T]): Live[Boolean]
//
//  /** Returns `'''true'''` iff `y` comes before `x` in the ordering.
//   */
//  def gteq(x: Live[T], y: Live[T]): Live[Boolean] = lteq(y, x)
//
//  /** Returns `'''true'''` iff `x` comes before `y` in the ordering
//   *  and is not the same as `y`.
//   */
//  def lt(x: Live[T], y: Live[T]): Live[Boolean] = lteq(x, y) && !equiv(x, y)
//
//  /** Returns `'''true'''` iff `y` comes before `x` in the ordering
//   *  and is not the same as `x`.
//   */
//  def gt(x: Live[T], y: Live[T]): Live[Boolean] = gteq(x, y) && !equiv(x, y)
//
//  /** Returns `'''true'''` iff `x` is equivalent to `y` in the ordering.
//   */
//  override def equiv(x: Live[T], y: Live[T]): Live[Boolean] = lteq(x,y) && lteq(y,x)
//
//  def reverse : LivePartialOrdering[T] = new LivePartialOrdering[T] {
//    override def reverse = outer
//    def lteq(x: Live[T], y: Live[T]) = outer.lteq(y, x)
//    def tryCompare(x: Live[T], y: Live[T]) = outer.tryCompare(y, x)
//  }
//}
