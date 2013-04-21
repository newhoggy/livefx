package org.livefx.gap

import org.livefx.Debug
import org.livefx.debug._

final case class Branch[+A](ls: Trees[A], focus: Tree[A], rs: Trees[A]) extends Tree[A] {
  final override def empty: Branch[A] = throw new UnsupportedOperationException

  def removeL(): Tree[A] = throw new UnsupportedOperationException

  def removeR(): Tree[A] = throw new UnsupportedOperationException

  final override def itemL: A = focus.itemL

  final override def itemR: A = focus.itemR

  final override def remainingCapacity(implicit config: Config): Int = config.nodeCapacity - (ls.treeCount + rs.treeCount)

  final override def size: Int = ls.size + focus.size + rs.size

  final override def sizeL: Int = ls.size + focus.sizeL

  final override def sizeR: Int = rs.size + focus.sizeR

  final def shiftTo(index: Int): Branch[A] = {
    if (index < ls.treeCount) {
      val head = ls.head
      Branch[A](ls.tail, focus, head :: rs)
    } else if (index > ls.treeCount) {
      val head = rs.head
      Branch[A](head :: ls, focus, rs.tail)
    } else {
      this
    }
  }

  final override def divide(implicit config: Config): Either[(Tree[A], Tree[A]), (Tree[A], Tree[A])] = {
    val half = (ls.treeCount + rs.treeCount) / 2
    
    if (ls.treeCount > rs.treeCount) {
      val pivot = ls.treeCount - half
      val leftTrees = ls.drop(pivot)
      val rightTrees = ls.take(pivot)
      
      Left((
          Branch[A](
              leftTrees.tail,
              leftTrees.head,
              TreesNil),
          Branch[A](
              rightTrees,
              focus,
              rs)).postcondition(x => x._1.size + x._2.size == this.size))
    } else {
      val pivot = rs.treeCount - half
      val rightTrees = rs.drop(pivot)
      val leftTrees = rs.take(pivot)
      Left((
          Branch[A](
              ls,
              focus,
              leftTrees),
          Branch[A](
              TreesNil,
              rightTrees.head,
              rightTrees.tail)).postcondition(x => x._1.size + x._2.size == this.size))
    }
  }

  def pretty(inFocus: Boolean): String = s"${(s"${ls.size})" :: ls.trees.reverse.map(_.pretty(false)) ::: s"*${focus.pretty(inFocus)}*" :: rs.trees.map(_.pretty(false)) ::: s"(${rs.size}" :: List()).mkString("[", ", ", "]")} "
}
