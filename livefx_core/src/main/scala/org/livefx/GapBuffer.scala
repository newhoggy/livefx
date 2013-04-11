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
    println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }
  
  def itemL: A
  
  def itemR: A
  
  def sizeL: Int

  def sizeR: Int

  def size: Int
  
  def empty: GapTree[A]
  
  def remainingCapacity(implicit config: GapConfig): Int
  
  def divide(implicit config: GapConfig): (GapTree[A], GapTree[A])
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
  
  final override def insertL(value: A)(implicit config: GapConfig): GapTree[A] = branchesL match {
    case b::bs => {
      if (b.remainingCapacity > 0) {
        this.copy(branchesL = b.insertL(value)::bs)
      } else {
        val (b0, b1) = b.divide
        this.copy(branchesL = b0::b1::bs)
      }
    }
    case Nil => this.copy(branchesL = GapLeaf[A].insertL(value)::branchesL)
  }
  
  final override def insertR(value: A)(implicit config: GapConfig): GapTree[A] = branchesR match {
    case b::bs => {
      if (b.remainingCapacity > 0) {
        this.copy(branchesR = b.insertR(value)::bs)
      } else {
        val (b0, b1) = b.divide
        this.copy(branchesR = b0::b1::bs)
      }
    }
    case Nil => this.copy(branchesR = GapLeaf[A].insertR(value)::branchesR)
  }
  
  @tailrec
  final override def moveBy(steps: Int): Self = {
    println(s"moveBy($steps) in $this")
    if (steps > 0) {
      val head = branchesR.head
      if (steps > head.sizeL) {
        GapBranch(sizeL + head.size, head :: branchesL, focus, branchesR.tail, sizeR - head.size).moveBy(steps - head.sizeR)
      } else {
        GapBranch(sizeL, head.moveBy(steps) :: branchesR, focus, branchesR.tail, sizeR)
      }
    } else if (steps < 0) {
      val head = branchesL.head
      if (-steps > head.sizeR) {
        println(s"moveBy1($steps) in $this ${head.sizeR}")
        GapBranch(sizeL - head.size, branchesL.tail, focus, head :: branchesR, sizeR + head.size).moveBy(steps + head.sizeR)
      } else {
        println(s"moveBy2($steps) in $this")
        GapBranch(sizeL, head.moveBy(steps) :: branchesR, focus, branchesR.tail, sizeR)
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

  final override def divide(implicit config: GapConfig): (GapTree[A], GapTree[A]) = {
    (null, null)
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

  final override def divide(implicit config: GapConfig): (GapTree[A], GapTree[A]) = centre match { case c => (c.dropR, c.dropL) }
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
      val (b0, b1) = child.divide
      this.copy(child = GapBranch(b0.size, List(b0), GapLeaf[A](), List(b1), b1.size)).insertL(value)
    }
  }
  
  final def insertR(value: A): GapRoot[A] = {
    if (child.remainingCapacity > 0) {
      this.copy(child = child.insertR(value))
    } else {
      val (b0, b1) = child.divide
      this.copy(child = GapBranch(b0.size, List(b0), GapLeaf[A](), List(b1), b1.size)).insertR(value)
    }
  }

  final def moveBy(steps: Int): GapRoot[A] = this.copy(child = child.moveBy(steps))
  
  final def moveTo(index: Int): GapRoot[A] = {
    println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }

  final def itemL: A = child.itemL
  
  final def itemR: A = child.itemR
  
  final def sizeL: Int = child.sizeL

  final def sizeR: Int = child.sizeR

  final def size: Int = child.size 
  
  final def empty: GapTree[A] = child.empty

  def iterator: Iterator[A] = new Iterator[A] {
    println(GapRoot.this)
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

class GapBuffer[A: ClassTag](_config: GapConfig = GapConfig(16)) extends Iterable[A] {
  private implicit val config = _config
  
  private var _tree: GapRoot[A] = GapRoot[A]()
  
  def tree = _tree

  def insertL(value: A): Unit = _tree = _tree.insertL(value)

  def insertR(value: A): Unit = _tree = _tree.insertR(value)

  def moveBy(steps: Int): Unit = {
    if (steps != 0) {
      if (steps < 0) {
        if (steps + _tree.size < 0) {
          throw new IndexOutOfBoundsException
        }
      } else {
        if (steps - _tree.size < 0) {
          throw new IndexOutOfBoundsException
        }
      }

      _tree = _tree.moveBy(steps)
    }
  }
  
  def iterator: Iterator[A] = new Iterator[A] {
    private var tree = GapBuffer.this._tree.moveTo(0)
    final override def hasDefiniteSize: Boolean = true
    final override def length: Int = tree.sizeR
    final override def hasNext: Boolean = tree.sizeR != 0
    final override def next(): A = {
      tree = tree.moveBy(1)
      tree.itemL
    }
  }
}
