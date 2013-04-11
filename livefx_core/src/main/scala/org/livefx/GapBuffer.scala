package org.livefx

import scala.reflect.ClassTag
import scalaz.Monoid
import scala.collection.immutable.Stack
import scala.annotation.tailrec

case class GapConfig(val nodeCapacity: Int)

object Util {
  final def v[T](value: T): T = value
}

abstract class GapTree[A] {
  type Self <: GapTree[A]

  def insertL(value: A)(implicit config: GapConfig): GapTree[A]

  def insertR(value: A)(implicit config: GapConfig): GapTree[A]

  def moveBy(steps: Int): Self
  
  def moveTo(index: Int): Self = {
    if (Debug.debug) println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }
  
  def itemL: A
  
  def itemR: A
  
  def sizeL: Int

  def sizeR: Int

  def size: Int
  
  def empty: GapTree[A]
  
  def remainingCapacity(implicit config: GapConfig): Int
  
  def divide(implicit config: GapConfig): Either[(GapTree[A], GapTree[A]), (GapTree[A], GapTree[A])]
}

final case class GapBranch[A](sizeL: Int, branchesL: List[GapTree[A]], focus: GapTree[A], branchesR: List[GapTree[A]], sizeR: Int) extends GapTree[A] {
  type Self = GapBranch[A]
  
  private final def insertTreeL(tree: GapTree[A]): GapTree[A] = this.copy(sizeL = sizeL + tree.size, branchesL = tree::branchesL)
  
  private final def removeTreeL(): GapBranch[A] = {
    val removed = branchesL.head
    this.copy(sizeL = sizeL - removed.size, branchesL = branchesL.tail)
  }
  
  private final def withAtLeastOneTreeL(): GapTree[A] = if (branchesL.isEmpty) insertTreeL(branchesR.head.empty) else this
  
  private final def withAtLeastOneTreeR(): GapTree[A] = if (branchesR.isEmpty) insertTreeL(branchesL.head.empty) else this
  
  final override def empty: GapBranch[A] = throw new UnsupportedOperationException
  
  final override def insertL(value: A)(implicit config: GapConfig): GapTree[A] = {
    if (focus.remainingCapacity > 0) {
      this.copy(focus = focus.insertL(value))
    } else {
      focus.divide match {
        case Left((b0, b1)) => this.copy(focus = b0, branchesL = b1::branchesL)
        case Right((b0, b1)) => this.copy(focus = b0, branchesL = b1::branchesL)
      }
    }
  }
  
  final override def insertR(value: A)(implicit config: GapConfig): GapTree[A] = {
    println("--> enter insertR")
    try {
      if (focus.remainingCapacity > 0) {
        println("--> a")
        this.copy(focus = focus.insertR(value))
      } else {
        focus.divide match {
          case Left((b0, b1)) => {
            println("--> b")
            this.copy(focus = b0, branchesL = b1::branchesL)
          }
          case Right((b0, b1)) => {
            println("--> c")
            this.copy(focus = b0, branchesL = b1::branchesL)
          }
        }
      }
    } finally {
      println("--> exit insertR")
    }
  }
  
  @tailrec
  final override def moveBy(steps: Int): Self = {
    if (Debug.debug) println(s"moveBy($steps) in $this")
    if (steps > 0) {
      if (steps > focus.sizeL) {
        GapBranch(sizeL + focus.size, focus :: branchesL, branchesR.head, branchesR.tail, sizeR - focus.size).moveBy(steps - focus.sizeR)
      } else {
        GapBranch(sizeL, branchesL, focus.moveBy(steps), branchesR, sizeR)
      }
    } else if (steps < 0) {
      if (-steps > focus.sizeR) {
        if (Debug.debug) println(s"moveBy1($steps) in $this ${focus.sizeR}")
        GapBranch(sizeL - focus.size, branchesL.tail, branchesL.head, focus :: branchesR, sizeR + focus.size).moveBy(steps + focus.sizeR)
      } else {
        if (Debug.debug) println(s"moveBy2($steps) in $this")
        GapBranch(sizeL, branchesL, focus.moveBy(steps), branchesR, sizeR)
      }
    } else {
      this
    }
  }

  final override def itemL: A = branchesL.head.itemL
  
  final override def itemR: A = branchesR.head.itemR
  
  final override def remainingCapacity(implicit config: GapConfig): Int = config.nodeCapacity - (branchesL.size + branchesR.size)
  
  final override def size: Int = sizeL + sizeR
  
  final def shiftTo(index: Int): GapBranch[A] = {
    if (index < branchesL.size) {
      val head = branchesL.head
      GapBranch[A](sizeL - head.size, branchesL.tail, focus, head :: branchesR, sizeR + head.size)
    } else if (index > branchesL.size) {
      val head = branchesR.head
      GapBranch[A](sizeL + head.size, head :: branchesL, focus, branchesR.tail, sizeR - head.size)
    } else {
      this
    }
  }
  
  final def centre(implicit config: GapConfig): GapTree[A] = this.moveTo(config.nodeCapacity / 2)

  final override def divide(implicit config: GapConfig): Either[(GapTree[A], GapTree[A]), (GapTree[A], GapTree[A])] = {
    Left(null, null)
  }
}

case class GapLeaf[A](sizeL: Int, valuesL: List[A], valuesR: List[A], sizeR: Int) extends GapTree[A] {
  type Self = GapLeaf[A]

  final override def insertL(value: A)(implicit config: GapConfig): GapTree[A] = {
    assert(remainingCapacity > 0)
    this.copy(sizeL = sizeL + 1, valuesL = value::valuesL)
  }

  final override def insertR(value: A)(implicit config: GapConfig): GapTree[A] = {
    this.copy(sizeR = sizeR + 1, valuesR = value::valuesR)
  }
  
  final def removeL(): GapTree[A] = this.copy(sizeL = sizeL - 1, valuesL = valuesL.tail)

  final def removeR(): GapTree[A] = this.copy(sizeR = sizeR - 1, valuesR = valuesR.tail)
  
  final def getL: A = valuesL.head
  
  final def getR: A = valuesR.head
  
  final def empty: GapBranch[A] = GapBranch[A](0, Nil, GapLeaf[A](), Nil, 0)
  
  @tailrec
  final def moveBy(steps: Int): GapLeaf[A] = {
    if (steps > 0) {
      GapLeaf(sizeL + 1, valuesR.head :: valuesL, valuesR.tail, sizeR - 1).moveBy(steps - 1)
    } else if (steps < 0) {
      GapLeaf(sizeL - 1, valuesL.tail, valuesL.head :: valuesR, sizeR + 1).moveBy(steps + 1)
    } else {
      this
    }
  }
  
  final def itemL: A = valuesL.head

  final def itemR: A = valuesR.head
  
  final override def remainingCapacity(implicit config: GapConfig): Int = config.nodeCapacity - size
  
  final override def size: Int = sizeL + sizeR
  
  final def centre(implicit config: GapConfig): Self = this.moveTo(config.nodeCapacity / 2)
  
  final def dropL: GapLeaf[A] = GapLeaf(0, Nil, valuesR, sizeR)

  final def dropR: GapLeaf[A] = GapLeaf(sizeL, valuesL, Nil, 0)

  final override def divide(implicit config: GapConfig): Either[(GapTree[A], GapTree[A]), (GapTree[A], GapTree[A])] = {
    val half = size / 2
    if (sizeL >= half) {
      valuesL.takeRight(1)
      Left((
          GapLeaf[A](
              half,
              valuesL.drop(sizeL - half),
              valuesL.take(sizeL - half).reverse,
              sizeL - half),
          GapLeaf[A](0, Nil, valuesR, sizeR)))
    } else {
      Right((
          GapLeaf[A](sizeL, valuesL, Nil, 0),
          GapLeaf[A](
              sizeR - half,
              valuesR.take(sizeL - half).reverse,
              valuesR.drop(sizeL - half),
              half)))
    }
  }
}

object GapLeaf {
  def apply[A](valuesL: List[A], valuesR: List[A]): GapLeaf[A] = GapLeaf[A](valuesL.size, valuesL, valuesR, valuesR.size)
  def apply[A](): GapLeaf[A] = GapLeaf[A](0, Nil, Nil, 0)
}

final case class GapRoot[A](_config: GapConfig = GapConfig(16), child: GapTree[A] = GapLeaf[A]()) {
  private implicit val config = _config
  
  final def insertL(value: A): GapRoot[A] = {
    if (child.remainingCapacity > 0) {
      this.copy(child = child.insertL(value))
    } else {
      child.divide match {
        case Left((b0, b1)) => this.copy(child = GapBranch(b0.size, List(b0), GapLeaf[A](), List(b1), b1.size)).insertL(value)
        case Right((b0, b1)) => this.copy(child = GapBranch(b0.size, List(b0), GapLeaf[A](), List(b1), b1.size)).insertL(value)
      }
      
    }
  }
  
  final def insertR(value: A): GapRoot[A] = {
    if (child.remainingCapacity > 0) {
      val result = this.copy(child = child.insertR(value))
      println(s"--> $this.insertR($value) = $result")
      result
    } else {
      println("--> child.divide: " + child.divide)
      val x = child.divide match {
        case Left((l, focus)) => this.copy(child = GapBranch(l.size, List(l), focus, Nil, 0)).insertR(value)
        case Right((focus, r)) => this.copy(child = GapBranch(0, Nil, focus, List(r), r.size)).insertR(value)
      }
      println("--> result: " + x.child)
      x
    }
  }

  final def moveBy(steps: Int): GapRoot[A] = this.copy(child = child.moveBy(steps))
  
  final def moveTo(index: Int): GapRoot[A] = {
    if (Debug.debug) println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }

  final def itemL: A = child.itemL
  
  final def itemR: A = child.itemR
  
  final def sizeL: Int = child.sizeL

  final def sizeR: Int = child.sizeR

  final def size: Int = child.size 
  
  final def empty: GapTree[A] = child.empty

  def iterator: Iterator[A] = new Iterator[A] {
    if (Debug.debug) println(GapRoot.this)
    private var child: GapTree[A] = GapRoot.this.child.moveTo(0)
    final override def hasDefiniteSize: Boolean = true
    final override def length: Int = child.sizeR
    final override def hasNext: Boolean = child.sizeR != 0
    final override def next(): A = {
      child = child.moveBy(1)
      child.itemL
    }
  }
}
