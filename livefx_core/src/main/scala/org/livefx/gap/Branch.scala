package org.livefx.gap

import org.livefx.Debug
import org.livefx.debug._

final case class Branch[+A](ls: Trees[A], focus: Tree[A], rs: Trees[A]) extends Tree[A] {
  final override def itemL: A = focus.itemL

  final override def itemR: A = focus.itemR

  final override def remainingCapacity(implicit config: Config): Int = config.nodeCapacity - (ls.treeCount + rs.treeCount)

  final override def size: Int = ls.size + focus.size + rs.size

  final override def sizeL: Int = ls.size + focus.sizeL

  final override def sizeR: Int = rs.size + focus.sizeR

  def pretty(inFocus: Boolean): String = s"${(s"${ls.size})" :: ls.trees.reverse.map(_.pretty(false)) ::: s"*${focus.pretty(inFocus)}*" :: rs.trees.map(_.pretty(false)) ::: s"(${rs.size}" :: List()).mkString("[", ", ", "]")} "
}
