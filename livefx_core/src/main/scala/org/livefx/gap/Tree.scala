package org.livefx.gap

import org.livefx.debug._
import scala.annotation.tailrec
import org.livefx.LeftOrRight

abstract class Tree[+A] {
  @inline final def insertL[B >: A](value: B)(implicit config: Config): Tree[B] = Tree.insertL(this, value)

  @inline final def insertR[B >: A](value: B)(implicit config: Config): Tree[B] = Tree.insertR(this, value)

  @inline final def removeL(): Tree[A] = Tree.removeL(this)

  @inline final def removeR(): Tree[A] = Tree.removeR(this)

  @inline final def moveBy(steps: Int): Tree[A] = Tree.moveBy(this, steps)

  @inline final def moveTo(index: Int): Tree[A] = moveBy(index - sizeL)

  @inline final def divide(implicit config: Config): Either[(Tree[A], Tree[A]), (Tree[A], Tree[A])] = Tree.divide(this)

  def itemL: A

  def itemR: A

  def sizeL: Int

  def sizeR: Int

  def size: Int

  def remainingCapacity(implicit config: Config): Int

  def pretty(inFocus: Boolean): String
}

object Tree {
  def insertL[A, B >: A](self: Tree[A], value: B)(implicit config: Config): Tree[B] = {
    self match {
      case self@Branch(ls, focus, rs) => {
        if (self.focus.remainingCapacity > 0) {
          self.copy(focus = focus.insertL(value))
        } else {
          self.focus.divide match {
            case Left((l, newFocus)) => {
              self.copy(ls = l::ls, focus = newFocus.insertL(value))
            }
            case Right((newFocus, r)) => {
              self.copy(focus = newFocus.insertL(value), rs = r::rs)
            }
          }
        }
      }
      case self@Leaf(valuesL, valuesR) => {
        assert(self.remainingCapacity > 0)
        self.copy(valuesL = value::valuesL)
      }
    }
  } postcondition (_.size == self.size + 1)

  def insertR[A, B >: A](self: Tree[A], value: B)(implicit config: Config): Tree[B] = {
    self match {
      case self@Branch(ls, focus, rs) => {
        if (self.focus.remainingCapacity > 0) {
          self.copy(focus = focus.insertR(value))
        } else {
          self.focus.divide match {
            case Left((l, newFocus)) => {
              self.copy(ls = l::ls, focus = newFocus.insertR(value))
            }
            case Right((newFocus, r)) => {
              self.copy(focus = newFocus.insertR(value), rs = r::rs)
            }
          }
        }
      }
      case self@Leaf(valueL, valuesR) => {
        self.copy(valuesR = value::valuesR)
      }
    }
  } postcondition (_.size == self.size + 1)

  final def moveBy[A](self: Tree[A], steps: Int): Tree[A] = {
    if (steps > 0) {
      self match {
        case self@Leaf(valuesL, valuesR) => Leaf(valuesR.head :: valuesL, valuesR.tail).moveBy(steps - 1)
        case self@Branch(ls, focus, rs) => {
          if (steps > focus.sizeR) {
            Branch(focus :: ls, rs.head, rs.tail).moveBy(steps - rs.head.sizeL)
          } else {
            Branch(ls, focus.moveBy(steps), rs)
          }
        }
      }
    } else if (steps < 0) {
      self match {
        case self@Leaf(valuesL, valuesR) => Leaf(valuesL.tail, valuesL.head :: valuesR).moveBy(steps + 1)
        case self@Branch(ls, focus, rs) => {
          if (-steps > focus.sizeL) {
            Branch(ls.tail, ls.head, focus :: rs).moveBy(steps + focus.sizeL + ls.head.sizeR)
          } else {
            Branch(ls, focus.moveBy(steps), rs)
          }
        }
      }
    } else {
      self
    }
  }

  final def divide[A](self: Tree[A])(implicit config: Config): Either[(Tree[A], Tree[A]), (Tree[A], Tree[A])] = {
    self match {
      case self@Leaf(valuesL, valuesR) => {
        val half = self.size / 2
        if (self.sizeL >= half) {
          val pivot = self.sizeL - half
          Left((
              Leaf[A](valuesL.drop(pivot), valuesL.take(pivot).reverse),
              Leaf[A](ItemsNil, valuesR)))
        } else {
          val pivot = self.sizeR - half
          Right((
              Leaf[A](valuesL, ItemsNil), 
              Leaf[A](valuesR.take(pivot).reverse, valuesR.drop(pivot))))
        }.postcondition{case LeftOrRight((l, r)) => l.size + r.size == self.size}
      }
      case self@Branch(ls, focus, rs) => {
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
                  rs)).postcondition(x => x._1.size + x._2.size == self.size))
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
                  rightTrees.tail)).postcondition(x => x._1.size + x._2.size == self.size))
        }
      }
    }
  }

  final def branchLoad[A](tree: Tree[A]): scala.collection.immutable.Map[Int, Int] = {
    import scalaz._
    import Scalaz._
    tree match {
      case branch: Branch[A] => branch.ls.trees.foldLeft(Map[Int, Int]())((map, b) => map |+| branchLoad(b) + (branch.ls.trees.length + branch.rs.trees.length -> 1))
      case leaf: Leaf[A] => Map[Int, Int]()
    }
  }

  final def removeL[A](self: Tree[A]): Tree[A] = {
    self match {
      case self@Leaf(valuesL, valuesR) => self.copy(valuesL = valuesL.tail)
      case self@Branch(ls, focus, rs) => throw new UnsupportedOperationException
    }
  }

  final def removeR[A](self: Tree[A]): Tree[A] = {
    self match {
      case self@Leaf(valuesL, valuesR) => self.copy(valuesR = valuesR.tail)
      case self@Branch(ls, focus, rs) => throw new UnsupportedOperationException
    }
  }
}
