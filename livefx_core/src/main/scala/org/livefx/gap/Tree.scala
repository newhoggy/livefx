package org.livefx.gap

import org.livefx.debug._
import scala.annotation.tailrec

abstract class Tree[+A] {
  @inline final def insertL[B >: A](value: B)(implicit config: Config): Tree[B] = Tree.insertL(this, value)

  @inline final def insertR[B >: A](value: B)(implicit config: Config): Tree[B] = Tree.insertR(this, value)

  def removeL(): Tree[A]

  def removeR(): Tree[A]

  @inline final def moveBy(steps: Int): Tree[A] = Tree.moveBy(this, steps)

  @inline final def moveTo(index: Int): Tree[A] = moveBy(index - sizeL)

  def itemL: A

  def itemR: A

  def sizeL: Int

  def sizeR: Int

  def size: Int

  def empty: Tree[A]

  def remainingCapacity(implicit config: Config): Int

  def divide(implicit config: Config): Either[(Tree[A], Tree[A]), (Tree[A], Tree[A])]

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
}
