package org.livefx.gap

import org.livefx.Debug
import org.livefx.debug._

final case class Branch[+A](treesSizeL: Int, ls: Trees[A], focus: Tree[A], rs: Trees[A], treesSizeR: Int) extends Tree[A] {
  assert(treesSizeL == ls.trees.map(_.size).sum)
  assert(treesSizeR == rs.trees.map(_.size).sum)
  
  private final def insertTreeL[B >: A](tree: Tree[B]): Tree[B] = this.copy(treesSizeL = treesSizeL + tree.size, ls = tree::ls)
  
  private final def removeTreeL(): Branch[A] = {
    val removed = ls.head
    this.copy(treesSizeL = treesSizeL - removed.size, ls = ls.tail)
  }
  
  private final def withAtLeastOneTreeL(): Tree[A] = if (ls.isEmpty) insertTreeL(rs.head.empty) else this
  
  private final def withAtLeastOneTreeR(): Tree[A] = if (rs.isEmpty) insertTreeL(ls.head.empty) else this
  
  final override def empty: Branch[A] = throw new UnsupportedOperationException
  
  final override def insertL[B >: A](value: B)(implicit config: Config): Tree[B] = {
    Debug.print("branch.insertL")
    if (focus.remainingCapacity > 0) {
      Debug.print("branch.insertL 1")
      this.copy(focus = focus.insertL(value))
    } else {
      focus.divide match {
        case Left((l, newFocus)) => {
          this.copy(treesSizeL = l.size + treesSizeL, ls = l::ls, focus = newFocus.insertL(value))
        }
        case Right((newFocus, r)) => {
          Debug.print("branch.insertL 3")
          this.copy(focus = newFocus.insertL(value), rs = r::rs, treesSizeR = r.size + treesSizeR)
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
          this.copy(treesSizeL = l.size + treesSizeL, ls = l::ls, focus = newFocus.insertR(value))
        }
        case Right((newFocus, r)) => {
          Debug.print("branch.insertL 3")
          this.copy(focus = newFocus.insertR(value), rs = r::rs, treesSizeR = r.size + treesSizeR)
        }
      }
    } postcondition (_.size == this.size + 1)
  }
  
//  @tailrec
  final override def moveBy(steps: Int): Branch[A] = {
    Debug.trace(s"moveBy($steps) in ${this.pretty(true)}") {
      if (steps > 0) {
        if (steps > focus.sizeR) {
          Debug.trace("moveBy[1]") {
            Branch(treesSizeL + focus.size, focus :: ls, rs.head, rs.tail, treesSizeR - rs.head.size).moveBy(steps - rs.head.sizeL)
          }
        } else {
          Debug.trace("moveBy[2]") {
            val result = Branch(treesSizeL, ls, focus.moveBy(steps), rs, treesSizeR)
            Debug.print(s"moveBy[2]: $result")
            result
          }
        }
      } else if (steps < 0) {
        Debug.print(s"focus.sizeR = ${focus.sizeR}")
        if (-steps > focus.sizeL) {
          Debug.trace(s"moveBy1($steps) in ${this.pretty(true)} ${focus.sizeR}") {
            Branch(treesSizeL - ls.head.size, ls.tail, ls.head, focus :: rs, treesSizeR + focus.size).moveBy(steps + focus.sizeL + ls.head.sizeR)
          }
        } else {
          Debug.trace(s"moveBy2($steps) in ${this.pretty(true)}") {
            Branch(treesSizeL, ls, focus.moveBy(steps), rs, treesSizeR)
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
  
  final override def size: Int = treesSizeL + focus.size + treesSizeR
  
  final override def sizeL: Int = treesSizeL + focus.sizeL
  
  final override def sizeR: Int = treesSizeR + focus.sizeR
  
  final def shiftTo(index: Int): Branch[A] = {
    if (index < ls.treeCount) {
      val head = ls.head
      Branch[A](treesSizeL - head.size, ls.tail, focus, head :: rs, treesSizeR + head.size)
    } else if (index > ls.treeCount) {
      val head = rs.head
      Branch[A](treesSizeL + head.size, head :: ls, focus, rs.tail, treesSizeR - head.size)
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
              leftTrees.tail.trees.map(_.size).sum,
              leftTrees.tail,
              leftTrees.head,
              TreesNil,
              0),
          Branch[A](
              rightTrees.trees.map(_.size).sum,
              rightTrees,
              focus,
              rs,
              treesSizeR)).postcondition(x => x._1.size + x._2.size == this.size))
    } else {
      val pivot = rs.treeCount - half
      val rightTrees = rs.drop(pivot)
      val leftTrees = rs.take(pivot)
      Left((
          Branch[A](
              treesSizeL,
              ls,
              focus,
              leftTrees,
              leftTrees.trees.foldLeft(0)((a, b) => a + b.size)),
          Branch[A](
              0,
              TreesNil,
              rightTrees.head,
              rightTrees.tail,
              rightTrees.tail.trees.foldLeft(0)((a, b) => a + b.size))).postcondition(x => x._1.size + x._2.size == this.size))
    }
  }
  
  def pretty(inFocus: Boolean): String = s"${(s"$treesSizeL)" :: ls.trees.reverse.map(_.pretty(false)) ::: s"*${focus.pretty(inFocus)}*" :: rs.trees.map(_.pretty(false)) ::: s"($treesSizeR" :: List()).mkString("[", ", ", "]")} "
}
