package org.livefx.gap

import org.livefx.Debug
import scalaz.Scalaz.mkIdentity

case class GapConfig(val nodeCapacity: Int)

abstract class GapTree[+A] {
  def insertL[B >: A](value: B)(implicit config: GapConfig): GapTree[B]

  def insertR[B >: A](value: B)(implicit config: GapConfig): GapTree[B]

  def moveBy(steps: Int): GapTree[A]
  
  def moveTo(index: Int): GapTree[A]
  
  def itemL: A
  
  def itemR: A
  
  def sizeL: Int

  def sizeR: Int

  def size: Int
  
  def empty: GapTree[A]
  
  def remainingCapacity(implicit config: GapConfig): Int
  
  def divide(implicit config: GapConfig): Either[(GapTree[A], GapTree[A]), (GapTree[A], GapTree[A])]
  
  def pretty(inFocus: Boolean): String
}

final case class GapTrees[+A](trees: List[GapTree[A]]) {
  def ::[B >: A](tree: GapTree[B]): GapTrees[B] = this.copy(trees = tree::trees)
}


final case class GapBranch[+A](treesSizeL: Int, ls: List[GapTree[A]], focus: GapTree[A], rs: List[GapTree[A]], treesSizeR: Int) extends GapTree[A] {
  assert(treesSizeL == ls.map(_.size).sum)
  assert(treesSizeR == rs.map(_.size).sum)
  
  private final def insertTreeL[B >: A](tree: GapTree[B]): GapTree[B] = this.copy(treesSizeL = treesSizeL + tree.size, ls = tree::ls)
  
  private final def removeTreeL(): GapBranch[A] = {
    val removed = ls.head
    this.copy(treesSizeL = treesSizeL - removed.size, ls = ls.tail)
  }
  
  private final def withAtLeastOneTreeL(): GapTree[A] = if (ls.isEmpty) insertTreeL(rs.head.empty) else this
  
  private final def withAtLeastOneTreeR(): GapTree[A] = if (rs.isEmpty) insertTreeL(ls.head.empty) else this
  
  final override def empty: GapBranch[A] = throw new UnsupportedOperationException
  
  final override def insertL[B >: A](value: B)(implicit config: GapConfig): GapTree[B] = {
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
  
  final override def insertR[B >: A](value: B)(implicit config: GapConfig): GapTree[B] = {
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
    }
  }
  
//  @tailrec
  final override def moveBy(steps: Int): GapBranch[A] = {
    Debug.trace(s"moveBy($steps) in ${this.pretty(true)}") {
      if (steps > 0) {
        if (steps > focus.sizeR) {
          Debug.trace("moveBy[1]") {
            GapBranch(treesSizeL + focus.size, focus :: ls, rs.head, rs.tail, treesSizeR - rs.head.size).moveBy(steps - rs.head.sizeL)
          }
        } else {
          Debug.trace("moveBy[2]") {
            val result = GapBranch(treesSizeL, ls, focus.moveBy(steps), rs, treesSizeR)
            Debug.print(s"moveBy[2]: $result")
            result
          }
        }
      } else if (steps < 0) {
        Debug.print(s"focus.sizeR = ${focus.sizeR}")
        if (-steps > focus.sizeL) {
          Debug.trace(s"moveBy1($steps) in ${this.pretty(true)} ${focus.sizeR}") {
            GapBranch(treesSizeL - ls.head.size, ls.tail, ls.head, focus :: rs, treesSizeR + focus.size).moveBy(steps + focus.sizeL + ls.head.sizeR)
          }
        } else {
          Debug.trace(s"moveBy2($steps) in ${this.pretty(true)}") {
            GapBranch(treesSizeL, ls, focus.moveBy(steps), rs, treesSizeR)
          }
        }
      } else {
        this
      }
    }
  }

  final override def moveTo(index: Int): GapBranch[A] = {
    if (Debug.debug) println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }

  final override def itemL: A = focus.itemL
  
  final override def itemR: A = focus.itemR
  
  final override def remainingCapacity(implicit config: GapConfig): Int = config.nodeCapacity - (ls.size + rs.size)
  
  final override def size: Int = treesSizeL + focus.size + treesSizeR
  
  final override def sizeL: Int = treesSizeL + focus.sizeL
  
  final override def sizeR: Int = treesSizeR + focus.sizeR
  
  final def shiftTo(index: Int): GapBranch[A] = {
    if (index < ls.size) {
      val head = ls.head
      GapBranch[A](treesSizeL - head.size, ls.tail, focus, head :: rs, treesSizeR + head.size)
    } else if (index > ls.size) {
      val head = rs.head
      GapBranch[A](treesSizeL + head.size, head :: ls, focus, rs.tail, treesSizeR - head.size)
    } else {
      this
    }
  }
  
  final def centre(implicit config: GapConfig): GapTree[A] = this.moveTo(config.nodeCapacity / 2)
  
  final override def divide(implicit config: GapConfig): Either[(GapTree[A], GapTree[A]), (GapTree[A], GapTree[A])] = {
    val half = (ls.size + rs.size) / 2

    if (ls.size > rs.size) {
      val leftTrees = ls.drop(ls.size - half)
      val rightTrees = ls.take(ls.size - half)
      Left((
          GapBranch[A](
              leftTrees.tail.map(_.size).sum,
              leftTrees.tail,
              leftTrees.head,
              Nil,
              0),
          GapBranch[A](
              rightTrees.map(_.size).sum,
              rightTrees,
              focus,
              rs,
              treesSizeR)))
    } else {
      val rightTrees = rs.drop(rs.size - half)
      val leftTrees = rs.take(rs.size - half)
      Left((
          GapBranch[A](
              treesSizeL,
              ls,
              focus,
              leftTrees,
              leftTrees.foldLeft(0)((a, b) => a + b.size)),
          GapBranch[A](
              0,
              Nil,
              rightTrees.head,
              rightTrees.tail,
              rightTrees.tail.foldLeft(0)((a, b) => a + b.size))))
    }
  }
  
  def pretty(inFocus: Boolean): String = s"${(s"$treesSizeL)" :: ls.reverse.map(_.pretty(false)) ::: s"*${focus.pretty(inFocus)}*" :: rs.map(_.pretty(false)) ::: s"($treesSizeR" :: List()).mkString("[", ", ", "]")} "
}

case class GapLeaf[A](sizeL: Int, valuesL: List[A], valuesR: List[A], sizeR: Int) extends GapTree[A] {
  type Self = GapLeaf[A]
  
  assert(sizeL == valuesL.size)
  assert(sizeR == valuesR.size)

  final override def insertL[B >: A](value: B)(implicit config: GapConfig): GapTree[B] = {
    assert(remainingCapacity > 0)
    this.copy(sizeL = sizeL + 1, valuesL = value::valuesL)
  }

  final override def insertR[B >: A](value: B)(implicit config: GapConfig): GapTree[B] = {
    this.copy(sizeR = sizeR + 1, valuesR = value::valuesR)
  }
  
  final def removeL(): GapTree[A] = this.copy(sizeL = sizeL - 1, valuesL = valuesL.tail)

  final def removeR(): GapTree[A] = this.copy(sizeR = sizeR - 1, valuesR = valuesR.tail)
  
  final def getL: A = valuesL.head
  
  final def getR: A = valuesR.head
  
  final def empty: GapBranch[A] = GapBranch[A](0, Nil, GapLeaf[A](), Nil, 0)
  
//  @tailrec
  final def moveBy(steps: Int): GapLeaf[A] = {
    if (steps > 0) {
      Debug.trace(s"moveBy[leaf-r]($steps): $this") {
        val result = GapLeaf(sizeL + 1, valuesR.head :: valuesL, valuesR.tail, sizeR - 1).moveBy(steps - 1)
        Debug.print(s"moveBy[leaf-result]: $result")
        result
      }
    } else if (steps < 0) {
      Debug.trace(s"moveBy[leaf-l]($steps): $this") {
        val result = GapLeaf(sizeL - 1, valuesL.tail, valuesL.head :: valuesR, sizeR + 1).moveBy(steps + 1)
        Debug.print(s"moveBy[leaf-result]: $result")
        result
      }
    } else {
      Debug.trace(s"moveBy[leaf-0]($steps): $this") {
        this
      }
    }
  }
  
  final override def moveTo(index: Int): GapLeaf[A] = {
    if (Debug.debug) println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }

  final def itemL: A = valuesL.head

  final def itemR: A = valuesR.head
  
  final override def remainingCapacity(implicit config: GapConfig): Int = config.nodeCapacity - size
  
  final override def size: Int = sizeL + sizeR
  
  final def centre(implicit config: GapConfig): GapLeaf[A] = this.moveTo(config.nodeCapacity / 2)
  
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
      val result = Right((
          GapLeaf[A](sizeL, valuesL, Nil, 0),
          GapLeaf[A](
              sizeR - half,
              valuesR.take(sizeR - half).reverse,
              valuesR.drop(sizeR - half),
              half)))
      result
    }
  }

  def pretty(inFocus: Boolean): String = s"${(s"$sizeL)" :: valuesL.reverse.map(_.toString) ::: (if (inFocus) "*-*" else "*") :: valuesR.map(_.toString) ::: s"($sizeR" :: List()).mkString("[", ", ", "]")}"
}

object GapLeaf {
  def apply[A](valuesL: List[A], valuesR: List[A]): GapLeaf[A] = GapLeaf[A](valuesL.size, valuesL, valuesR, valuesR.size)
  def apply[A](): GapLeaf[A] = GapLeaf[A](0, Nil, Nil, 0)
}

final case class GapRoot[A](_config: GapConfig = GapConfig(16), child: GapTree[A] = GapLeaf[A]()) {
  private implicit val config = _config
  
  final def insertL(value: A): GapRoot[A] = {
    if (child.remainingCapacity > 0) {
      Debug.trace(s"insertL($value)", this.child.pretty(true)) {
        val result = this.copy(child = child.insertL(value))
        Debug.print("root-result: " + result.child.pretty(true))
        result
      }
    } else {
      Debug.print("insertL divide")
      child.divide match {
        case Left((l, focus)) => {
          val result = this.copy(child = GapBranch(l.size, List(l), focus, List(), 0)).insertL(value)
          Debug.print("result: " +  result.child.pretty(true))
          result
        }
        case Right((focus, r)) => this.copy(child = GapBranch(0, List(), focus, List(r), r.size)).insertL(value)
      }
    }
  }
  
  final def insertR(value: A): GapRoot[A] = {
    if (child.remainingCapacity > 0) {
      this.copy(child = child.insertR(value))
    } else {
      child.divide match {
        case Left((l, focus)) => this.copy(child = GapBranch(l.size, List(l), focus, Nil, 0).insertR(value))
        case Right((focus, r)) => this.copy(child = GapBranch(0, Nil, focus, List(r), r.size).insertR(value))
      }
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

  def branchLoad = GapBuffer.branchLoad(child)
}

object GapBuffer {
  def branchLoad[A](tree: GapTree[A]): scala.collection.immutable.Map[Int, Int] = {
    import scalaz._
    import Scalaz._
    tree match {
      case branch: GapBranch[A] => branch.ls.foldLeft(Map[Int, Int]())((map, b) => map |+| branchLoad(b) + (branch.ls.length + branch.rs.length -> 1))
      case leaf: GapLeaf[A] => Map[Int, Int]()
    }
  }
}
