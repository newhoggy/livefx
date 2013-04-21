package org.livefx.gap

import org.livefx.Debug
import org.livefx.debug._

final case class Branch[+A](ls: Trees[A], focus: Tree[A], rs: Trees[A]) extends Tree[A] {
  final override def empty: Branch[A] = throw new UnsupportedOperationException

  final override def insertL[B >: A](value: B)(implicit config: Config): Tree[B] = {
    Debug.print("branch.insertL")
    if (focus.remainingCapacity > 0) {
      Debug.print("branch.insertL 1")
      this.copy(focus = focus.insertL(value))
    } else {
      focus.divide match {
        case Left((l, newFocus)) => {
          this.copy(ls = l::ls, focus = newFocus.insertL(value))
        }
        case Right((newFocus, r)) => {
          Debug.print("branch.insertL 3")
          this.copy(focus = newFocus.insertL(value), rs = r::rs)
        }
      }
    }
  }

  final override def insertR[B >: A](value: B)(implicit config: Config): Tree[B] = {
    if (focus.remainingCapacity > 0) {
      this.copy(focus = focus.insertR(value))
    } else {
      focus.divide match {
        case Left((l, newFocus)) => {
          this.copy(ls = l::ls, focus = newFocus.insertR(value))
        }
        case Right((newFocus, r)) => {
          Debug.print("branch.insertL 3")
          this.copy(focus = newFocus.insertR(value), rs = r::rs)
        }
      }
    } postcondition (_.size == this.size + 1)
  }

  def removeL(): Tree[A] = throw new UnsupportedOperationException

  def removeR(): Tree[A] = throw new UnsupportedOperationException

  //  @tailrec
  final override def moveBy(steps: Int): Branch[A] = {
    Debug.trace(s"moveBy($steps) in ${this.pretty(true)}") {
      if (steps > 0) {
        if (steps > focus.sizeR) {
          Debug.trace("moveBy[1]") {
            Branch(focus :: ls, rs.head, rs.tail).moveBy(steps - rs.head.sizeL)
          }
        } else {
          Debug.trace("moveBy[2]") {
            val result = Branch(ls, focus.moveBy(steps), rs)
            Debug.print(s"moveBy[2]: $result")
            result
          }
        }
      } else if (steps < 0) {
        Debug.print(s"focus.sizeR = ${focus.sizeR}")
        if (-steps > focus.sizeL) {
          Debug.trace(s"moveBy1($steps) in ${this.pretty(true)} ${focus.sizeR}") {
            Branch(ls.tail, ls.head, focus :: rs).moveBy(steps + focus.sizeL + ls.head.sizeR)
          }
        } else {
          Debug.trace(s"moveBy2($steps) in ${this.pretty(true)}") {
            Branch(ls, focus.moveBy(steps), rs)
          }
        }
      } else {
        this
      }
    }
  }

  final override def moveTo(index: Int): Branch[A] = {
    if (Debug.debug) println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }

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

  final def centre(implicit config: Config): Tree[A] = this.moveTo(config.nodeCapacity / 2)

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
