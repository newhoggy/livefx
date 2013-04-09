package org.livefx

import scala.reflect.ClassTag
import scalaz.Monoid
import scala.collection.immutable.Stack
import scala.annotation.tailrec

abstract class GapTree[A] {
  def insertL(value: A): GapTree[A]
  
  def insertR(value: A): GapTree[A]

  def moveBy(steps: Int): GapTree[A]

  def sizeL: Int

  def sizeR: Int

  def size: Int
  
  def empty: GapTree[A]
}

case class GapBranch[A](sizeL: Int, branchesL: List[GapTree[A]], branchesR: List[GapTree[A]], sizeR: Int) extends GapTree[A] {
  private final def insertTreeL(tree: GapTree[A]): GapTree[A] = this.copy(sizeL = sizeL + tree.size, branchesL = tree::branchesL)
  
  private final def removeTreeL(): GapBranch[A] = {
    val removed = branchesL.head
    this.copy(sizeL = sizeL - removed.size, branchesL = branchesL.tail)
  }
  
  private final def withAtLeastOneTreeL(): GapTree[A] = if (branchesL.isEmpty) insertTreeL(branchesR.head.empty) else this
  
  final def empty: GapBranch[A] = GapBranch[A](0, Nil, Nil, 0)
  
  final override def insertL(value: A): GapTree[A] = branchesL match {
    case b::bs => this.copy(branchesL = b.insertL(value)::bs)
    case Nil => this.copy(branchesL = GapLeaf[A].insertL(value)::branchesL)
  }
  
  final override def insertR(value: A): GapTree[A] = branchesR match {
    case b::bs => this.copy(branchesR = b.insertR(value)::bs)
    case Nil => this.copy(branchesR = GapLeaf[A].insertR(value)::branchesR)
  }
  
  @tailrec
  final def moveBy(steps: Int): GapTree[A] = {
    if (steps > 0) {
      val head = branchesR.head
      if (steps > head.sizeL) {
        GapBranch(sizeL + 1, head :: branchesR, branchesR.tail, sizeR - 1).moveBy(steps + 1)
      } else {
        GapBranch(sizeL, head.moveBy(steps) :: branchesR, branchesR.tail, sizeR)
      }
    } else if (steps < 0) {
      val head = branchesL.head
      GapBranch(sizeL - 1, branchesL.tail, head :: branchesR, sizeR + 1).moveBy(steps - 1)
    } else {
      this
    }
  }

  final override def size: Int = sizeL + sizeR
}

case class GapLeaf[A](sizeL: Int, valuesL: List[A], valuesR: List[A], sizeR: Int) extends GapTree[A] {
  final override def insertL(value: A): GapTree[A] = this.copy(sizeL = sizeL + 1, valuesL = value::valuesL)
  
  final def insertR(value: A): GapTree[A] = this.copy(sizeR = sizeR + 1, valuesR = value::valuesR)
  
  final def removeL(): GapTree[A] = this.copy(sizeL = sizeL - 1, valuesL = valuesL.tail)

  final def removeR(): GapTree[A] = this.copy(sizeR = sizeR - 1, valuesR = valuesR.tail)
  
  final def getL: A = valuesL.head
  
  final def getR: A = valuesR.head
  
  final def empty: GapBranch[A] = GapBranch[A](0, Nil, Nil, 0)
  
  @tailrec
  final def moveBy(steps: Int): GapLeaf[A] = {
    if (steps > 0) {
      GapLeaf(sizeL + 1, valuesR.head :: valuesL, valuesR.tail, sizeR - 1).moveBy(steps + 1)
    } else if (steps < 0) {
      GapLeaf(sizeL - 1, valuesL.tail, valuesL.head :: valuesR, sizeR + 1).moveBy(steps - 1)
    } else {
      this
    }
  }
  
  final def moveTo(index: Int): GapLeaf[A] = moveBy(index - sizeL)
  
  final override def size: Int = sizeL + sizeR
}

object GapLeaf {
  def apply[A](valuesL: List[A], valuesR: List[A]): GapLeaf[A] = GapLeaf[A](valuesL.size, valuesL, valuesR, valuesR.size)
  def apply[A](): GapLeaf[A] = GapLeaf[A](0, Nil, Nil, 0)
}

class GapBuffer[A: ClassTag] {
  var tree: GapTree[A] = GapLeaf[A](Nil, Nil)

  def insertL(value: A): Unit = tree = tree.insertL(value)

  def moveBy(steps: Int): Unit = {
    if (steps != 0) {
      if (steps < 0) {
        if (steps + tree.size < 0) {
          throw new IndexOutOfBoundsException
        }
      } else {
        if (steps - tree.size < 0) {
          throw new IndexOutOfBoundsException
        }
      }

      tree = tree.moveBy(steps)
    }
  }
}
