package org.livefx.volume

import scala.annotation.tailrec
import scalaz.Monoid
import scala.annotation.meta.getter

sealed trait Color {
  def apply[T](l: Tree[T], v: T, r: Tree[T]): Tree[T]

  final def balanceLeft[B, B1 >: B](l: Tree[B1], zv: B, d: Tree[B1]): Tree[B1] = l match {
    case Red(Red(lll, llv, llr), lv, lr) => Red(Black(lll, llv, llr), lv, Black(lr, zv, d))
    case Red(ll, lv, Red(lrl, lrv, lrr)) => Red(Black(ll, lv, lrl), lrv, Black(lrr, zv, d))
    case _ => this(l, zv, d)
  }

  final def balanceRight[B, B1 >: B](a: Tree[B1], xv: B, r: Tree[B1]): Tree[B1] = r match {
    case Red(Red(rll, rlv, rlr), rv, rr) => Red(Black(a, xv, rll), rlv, Black(rlr, rv, rr))
    case Red(rl, rv, Red(rrl, rrv, rrr)) => Red(Black(a, xv, rl), rv, Black(rrl, rrv, rrr))
    case _ => this(a, xv, r)
  }
}

final case object Black extends Color {
}

final case object Red extends Color {
}

sealed abstract class Tree[+B] extends Serializable {
  def value: B
  def left: Tree[B]
  def right: Tree[B]
  def blacken: Tree[B]
  def redden: Tree[B]
  def count: Int
  def color: Color

  final def is(color: Color): Boolean = this.color == color

  @inline final def insert[B1 >: B](index: Int, v: B1): Tree[B1] = this.ins(index, v).blacken
  @inline final def update[B1 >: B](index: Int, v: B1, overwrite: Boolean): Tree[B1] = this.upd(index, v, overwrite).blacken
  @inline final def delete(index: Int): Tree[B] = this.del(index).blacken

  final def subl: Tree[B] = {
    if (this.isInstanceOf[Black[_]]) this.redden
    else sys.error("Defect: invariance violation; expected black, got " + this)
  }

  final def get(index: Int): Option[B] = this.lookup(index) match {
    case Leaf => None
    case tree => Some(tree.value)
  }

  @tailrec
  final def lookup(index: Int): Tree[B] = {
    if (this == Leaf) Leaf
    else if (index < this.left.count) this.left.lookup(index)
    else if (index > this.left.count) this.right.lookup(index)
    else this
  }

  @tailrec
  final def nth(n: Int): Tree[B] = {
    val count = this.left.count
    if (n < count) this.left.nth(n)
    else if (n > count) this.right.nth(n - count - 1)
    else this
  }

  def updNth[B1 >: B](idx: Int, index: Int, v: B1, overwrite: Boolean): Tree[B1] = if (this == Leaf) {
    Red(Leaf, v, Leaf)
  } else {
    val rank = this.left.count + 1
    if (idx < rank) this.color.balanceLeft(this.left.updNth(idx, index, v, overwrite), this.value, this.right)
    else if (idx > rank) this.color.balanceRight(this.left, this.value, this.right.updNth(idx - rank, index, v, overwrite))
    else if (overwrite) this.color(this.left, v, this.right)
    else this
  }

  private[volume] def ins[B1 >: B](index: Int, v: B1): Tree[B1] = {
    if (this == Leaf) {
      Red(Leaf, v, Leaf)
    } else {
      if (index <= this.left.count) {
        this.color.balanceLeft(this.left.ins(index, v), this.value, this.right)
      } else if (index > this.left.count) {
        this.color.balanceRight(this.left, this.value, this.right.ins(index - this.left.count - 1, v))
      } else {
        this.color(this.left, v, this.right)
      }
    }
  }

  private def upd[B1 >: B](index: Int, v: B1, overwrite: Boolean): Tree[B1] = if (this == Leaf) {
    throw new IndexOutOfBoundsException
  } else {
    if (index < this.left.count) this.color.balanceLeft(this.left.upd(index, v, overwrite), this.value, this.right)
    else if (index > this.left.count) this.color.balanceRight(this.left, this.value, this.right.upd(index - this.left.count - 1, v, overwrite))
    else if (overwrite) this.color(this.left, v, this.right)
    else this
  }

  /* Based on Stefan Kahrs' Haskell version of Okasaki's Red&Black Trees
   * http://www.cse.unsw.edu.au/~dons/data/RedBlackTree.html */
  private def del(index: Int): Tree[B] = if (this == Leaf) Leaf else {
    def balance(tl: Tree[B], xv: B, tr: Tree[B]) = if (tl.is(Red)) {
      if (tr.is(Red)) {
        Red(tl.blacken, xv, tr.blacken)
      } else if (tl.left.is(Red)) {
        Red(tl.left.blacken, tl.value, Black(tl.right, xv, tr))
      } else if (tl.right.is(Red)) {
        Red(Black(tl.left, tl.value, tl.right.left), tl.right.value, Black(tl.right.right, xv, tr))
      } else {
        Black(tl, xv, tr)
      }
    } else if (tr.is(Red)) {
      if (tr.right.is(Red)) {
        Red(Black(tl, xv, tr.left), tr.value, tr.right.blacken)
      } else if (tr.left.is(Red)) {
        Red(Black(tl, xv, tr.left.left), tr.left.value, Black(tr.left.right, tr.value, tr.right))
      } else {
        Black(tl, xv, tr)
      }
    } else {
      Black(tl, xv, tr)
    }

    def balLeft(tl: Tree[B], xv: B, tr: Tree[B]) = if (tl.is(Red)) {
      Red(tl.blacken, xv, tr)
    } else if (tr.is(Black)) {
      balance(tl, xv, tr.redden)
    } else if (tr.is(Red) && tr.left.is(Black)) {
      Red(Black(tl, xv, tr.left.left), tr.left.value, balance(tr.left.right, tr.value, tr.right.subl))
    } else {
      sys.error("Defect: invariance violation")
    }
    def balRight(tl: Tree[B], xv: B, tr: Tree[B]) = if (tr.is(Red)) {
      Red(tl, xv, tr.blacken)
    } else if (tl.is(Black)) {
      balance(tl.redden, xv, tr)
    } else if (tl.is(Red) && tl.right.is(Black)) {
      Red(balance(tl.left.subl, tl.value, tl.right.left), tl.right.value, Black(tl.right.right, xv, tr))
    } else {
      sys.error("Defect: invariance violation")
    }
    def delLeft = if (this.left.is(Black)) balLeft(this.left.del(index), this.value, this.right) else Red(this.left.del(index), this.value, this.right)
    def delRight = if (this.right.is(Black)) balRight(this.left, this.value, this.right.del(index - this.left.count - 1)) else Red(this.left, this.value, this.right.del(index - this.left.count - 1))
    def append(tl: Tree[B], tr: Tree[B]): Tree[B] = if (tl == Leaf) {
      tr
    } else if (tr == Leaf) {
      tl
    } else if (tl.is(Red) && tr.is(Red)) {
      val bc = append(tl.right, tr.left)
      if (bc.is(Red)) {
        Red(Red(tl.left, tl.value, bc.left), bc.value, Red(bc.right, tr.value, tr.right))
      } else {
        Red(tl.left, tl.value, Red(bc, tr.value, tr.right))
      }
    } else if (tl.is(Black) && tr.is(Black)) {
      val bc = append(tl.right, tr.left)
      if (bc.is(Red)) {
        Red(Black(tl.left, tl.value, bc.left), bc.value, Black(bc.right, tr.value, tr.right))
      } else {
        balLeft(tl.left, tl.value, Black(bc, tr.value, tr.right))
      }
    } else if (tr.is(Red)) {
      Red(append(tl, tr.left), tr.value, tr.right)
    } else if (tl.is(Red)) {
      Red(tl.left, tl.value, append(tl.right, tr))
    } else {
      sys.error("unmatched tree on append: " + tl + ", " + tr)
    }

    if (index < this.left.count) delLeft
    else if (index > this.left.count) delRight
    else append(this.left, this.right)
  }
}

final case class Red[+B](left: Tree[B], value: B, right: Tree[B]) extends Tree[B] {
  override def blacken: Tree[B] = Black(left, value, right)
  override def redden: Tree[B] = this
  override def toString: String = "Red(" + value + ", " + left + ", " + right + ")"
  override def color: Color = Red
  final val count: Int = 1 + left.count + right.count
}

final case class Black[+B](left: Tree[B], value: B, right: Tree[B]) extends Tree[B] {
  override def blacken: Tree[B] = this
  override def redden: Tree[B] = Red(left, value, right)
  override def toString: String = "Black(" + value + ", " + left + ", " + right + ")"
  override def color: Color = Black
  final val count: Int = 1 + left.count + right.count
}

final case object Leaf extends Tree[Nothing] {
  def value: Nothing = throw new UnsupportedOperationException
  def left: Tree[Nothing] = throw new UnsupportedOperationException
  def right: Tree[Nothing] = throw new UnsupportedOperationException
  def count: Int = 0
  def blacken: Tree[Nothing] = Leaf.this
  def redden: Tree[Nothing] = throw new UnsupportedOperationException
  override def color: Color = Black
}

object Tree {
  def rangeImpl[B](tree: Tree[B], from: Option[Int], until: Option[Int]): Tree[B] = (from, until) match {
    case (Some(from), Some(until)) => this.range(tree, from, until)
    case (Some(from), None)        => this.from(tree, from)
    case (None,       Some(until)) => this.until(tree, until)
    case (None,       None)        => tree
  }
  def range[B](tree: Tree[B], from: Int, until: Int): Tree[B] = doRange(tree, from, until).blacken
  def from[B](tree: Tree[B], from: Int): Tree[B] = doFrom(tree, from).blacken
  def to[B](tree: Tree[B], to: Int): Tree[B] = doTo(tree, to).blacken
  def until[B](tree: Tree[B], key: Int): Tree[B] = doUntil(tree, key).blacken

  def drop[B](tree: Tree[B], n: Int): Tree[B] = doDrop(tree, n).blacken
  def take[B](tree: Tree[B], n: Int): Tree[B] = doTake(tree, n).blacken
  def slice[B](tree: Tree[B], from: Int, until: Int): Tree[B] = doSlice(tree, from, until).blacken

  def smallest[B](tree: Tree[B]): Tree[B] = {
    if (tree == Leaf) throw new NoSuchElementException("empty map")
    var result = tree
    while (result.left != Leaf) result = result.left
    result
  }
  def greatest[B](tree: Tree[B]): Tree[B] = {
    if (tree == Leaf) throw new NoSuchElementException("empty map")
    var result = tree
    while (result.right != Leaf) result = result.right
    result
  }

  def foreach[B, U](tree: Tree[B], f: B => U): Unit = if (tree != Leaf) {
    if (tree.left != Leaf) foreach(tree.left, f)
    f(tree.value)
    if (tree.right != Leaf) foreach(tree.right, f)
  }

  def iterator[_, B](tree: Tree[B]): Iterator[B] = new ValuesIterator(tree)

  private[this] def doFrom[B](tree: Tree[B], from: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    if (tree.left.count < from) return doFrom(tree.right, from - tree.left.count - 1)
    val newLeft = doFrom(tree.left, from)
    if (newLeft eq tree.left) tree
    else if (newLeft == Leaf) tree.right.upd(0, tree.value, false)
    else rebalance(tree, newLeft, tree.right)
  }
  private[this] def doTo[B](tree: Tree[B], to: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    if (to < tree.left.count) return doTo(tree.left, to)
    val newRight = doTo(tree.right, to)
    if (newRight eq tree.right) tree
    else if (newRight == Leaf) tree.left.upd(0, tree.value, false)
    else rebalance(tree, tree.left, newRight)
  }
  private[this] def doUntil[B](tree: Tree[B], until: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    if (until <= tree.left.count) return doUntil(tree.left, until)
    val newRight = doUntil(tree.right, until)
    if (newRight eq tree.right) tree
    else if (newRight == Leaf) tree.left.upd(0, tree.value, false)
    else rebalance(tree, tree.left, newRight)
  }
  private[this] def doRange[B](tree: Tree[B], from: Int, until: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    if (tree.left.count < from) return doRange(tree.right, from - tree.left.count - 1, until - tree.left.count - 1)
    if (until <= tree.left.count) return doRange(tree.left, from, until)
    val newLeft = doFrom(tree.left, from)
    val newRight = doUntil(tree.right, until - tree.left.count - 1)
    if ((newLeft eq tree.left) && (newRight eq tree.right)) tree
    else if (newLeft == Leaf) newRight.upd(0, tree.value, false)
    else if (newRight == Leaf) newLeft.upd(0, tree.value, false)
    else rebalance(tree, newLeft, newRight)
  }

  private[this] def doDrop[B](tree: Tree[B], n: Int): Tree[B] = {
    if (n <= 0) return tree
    if (n >= tree.count) return Leaf
    val count = tree.left.count
    if (n > count) return doDrop(tree.right, n - count - 1)
    val newLeft = doDrop(tree.left, n)
    if (newLeft eq tree.left) tree
    else if (newLeft == Leaf) tree.right.updNth(n - count - 1, 0, tree.value, false)
    else rebalance(tree, newLeft, tree.right)
  }
  private[this] def doTake[B](tree: Tree[B], n: Int): Tree[B] = {
    if (n <= 0) return Leaf
    if (n >= tree.count) return tree
    val count = tree.left.count
    if (n <= count) return doTake(tree.left, n)
    val newRight = doTake(tree.right, n - count - 1)
    if (newRight eq tree.right) tree
    else if (newRight == Leaf) tree.left.updNth(n, 0, tree.value, false)
    else rebalance(tree, tree.left, newRight)
  }
  private[this] def doSlice[B](tree: Tree[B], from: Int, until: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    val count = tree.left.count
    if (from > count) return doSlice(tree.right, from - count - 1, until - count - 1)
    if (until <= count) return doSlice(tree.left, from, until)
    val newLeft = doDrop(tree.left, from)
    val newRight = doTake(tree.right, until - count - 1)
    if ((newLeft eq tree.left) && (newRight eq tree.right)) tree
    else if (newLeft == Leaf) newRight.updNth(from - count - 1, 0, tree.value, false)
    else if (newRight == Leaf) newLeft.updNth(until, 0, tree.value, false)
    else rebalance(tree, newLeft, newRight)
  }

  // The zipper returned might have been traversed left-most (always the left child)
  // or right-most (always the right child). Left trees are traversed right-most,
  // and right trees are traversed leftmost.

  // Returns the zipper for the side with deepest black nodes depth, a flag
  // indicating whether the trees were unbalanced at all, and a flag indicating
  // whether the zipper was traversed left-most or right-most.

  // If the trees were balanced, returns an empty zipper
  private[this] def compareDepth[B](left: Tree[B], right: Tree[B]): (List[Tree[B]], Boolean, Boolean, Int) = {
    // Once a side is found to be deeper, unzip it to the bottom
    def unzip(zipper: List[Tree[B]], leftMost: Boolean): List[Tree[B]] = {
      val next = if (leftMost) zipper.head.left else zipper.head.right
      next match {
        case Leaf => zipper
        case node => unzip(node :: zipper, leftMost)
      }
    }

    // Unzip left tree on the rightmost side and right tree on the leftmost side until one is
    // found to be deeper, or the bottom is reached
    def unzipBoth(left: Tree[B],
                  right: Tree[B],
                  leftZipper: List[Tree[B]],
                  rightZipper: List[Tree[B]],
                  smallerDepth: Int): (List[Tree[B]], Boolean, Boolean, Int) = {
      if (left.is(Black) && right.is(Black)) {
        unzipBoth(left.right, right.left, left :: leftZipper, right :: rightZipper, smallerDepth + 1)
      } else if (left.is(Red) && right.is(Red)) {
        unzipBoth(left.right, right.left, left :: leftZipper, right :: rightZipper, smallerDepth)
      } else if (right.is(Red)) {
        unzipBoth(left, right.left, leftZipper, right :: rightZipper, smallerDepth)
      } else if (left.is(Red)) {
        unzipBoth(left.right, right, left :: leftZipper, rightZipper, smallerDepth)
      } else if (left == Leaf && (right == Leaf)) {
        (Nil, true, false, smallerDepth)
      } else if (left == Leaf && right.is(Black)) {
        val leftMost = true
        (unzip(right :: rightZipper, leftMost), false, leftMost, smallerDepth)
      } else if (left.is(Black) && right == Leaf) {
        val leftMost = false
        (unzip(left :: leftZipper, leftMost), false, leftMost, smallerDepth)
      } else {
        sys.error("unmatched trees in unzip: " + left + ", " + right)
      }
    }
    unzipBoth(left, right, Nil, Nil, 0)
  }

  private[this] def rebalance[B](tree: Tree[B], newLeft: Tree[B], newRight: Tree[B]) = {
    // This is like drop(n-1), but only counting black nodes
    def  findDepth(zipper: List[Tree[B]], depth: Int): List[Tree[B]] = zipper match {
      case head :: tail if head.is(Black) =>
        if (depth == 1) zipper else findDepth(tail, depth - 1)
      case _ :: tail => findDepth(tail, depth)
      case Nil => sys.error("Defect: unexpected empty zipper while computing range")
    }

    // Blackening the smaller tree avoids balancing problems on union;
    // this can't be done later, though, or it would change the result of compareDepth
    val blkNewLeft = newLeft.blacken
    val blkNewRight = newRight.blacken
    val (zipper, levelled, leftMost, smallerDepth) = compareDepth(blkNewLeft, blkNewRight)

    if (levelled) {
      Black(blkNewLeft, tree.value, blkNewRight)
    } else {
      val zipFrom = findDepth(zipper, smallerDepth)
      val union = if (leftMost) {
        Red(blkNewLeft, tree.value, zipFrom.head)
      } else {
        Red(zipFrom.head, tree.value, blkNewRight)
      }
      val zippedTree = zipFrom.tail.foldLeft(union: Tree[B]) { (tree, node) =>
        if (leftMost)
          node.color.balanceLeft(tree, node.value, node.right)
        else
          node.color.balanceRight(node.left, node.value, tree)
      }
      zippedTree
    }
  }

  private[this] abstract class TreeIterator[B, R](tree: Tree[B]) extends Iterator[R] {
    protected[this] def nextResult(tree: Tree[B]): R

    override def hasNext: Boolean = next != Leaf

    override def next: R = next match {
      case Leaf =>
        throw new NoSuchElementException("next on empty iterator")
      case tree =>
        next = findNext(tree.right)
        nextResult(tree)
    }

    @tailrec
    private[this] def findNext(tree: Tree[B]): Tree[B] = {
      if (tree == Leaf) popPath()
      else if (tree.left == Leaf) tree
      else {
        pushPath(tree)
        findNext(tree.left)
      }
    }

    private[this] def pushPath(tree: Tree[B]) {
      try {
        path(index) = tree
        index += 1
      } catch {
        case _: ArrayIndexOutOfBoundsException =>
          /*
           * Either the tree became unbalanced or we calculated the maximum height incorrectly.
           * To avoid crashing the iterator we expand the path array. Obviously this should never
           * happen...
           *
           * An exception handler is used instead of an if-condition to optimize the normal path.
           * This makes a large difference in iteration speed!
           */
          assert(index >= path.length)
          path :+= Leaf
          pushPath(tree)
      }
    }
    private[this] def popPath(): Tree[B] = if (index == 0) Leaf else {
      index -= 1
      path(index)
    }

    private[this] var path = if (tree == Leaf) null else {
      /*
       * According to "Ralf Hinze. Constructing red-black trees" [http://www.cs.ox.ac.uk/ralf.hinze/publications/#P5]
       * the maximum height of a red-black tree is 2*log_2(n + 2) - 2.
       *
       * According to {@see Integer#numberOfLeadingZeros} ceil(log_2(n)) = (32 - Integer.numberOfLeadingZeros(n - 1))
       *
       * We also don't store the deepest nodes in the path so the maximum path length is further reduced by one.
       */
      val maximumHeight = 2 * (32 - Integer.numberOfLeadingZeros(tree.count + 2 - 1)) - 2 - 1
      new Array[Tree[B]](maximumHeight)
    }
    private[this] var index = 0
    private[this] var next: Tree[B] = findNext(tree)
  }

  private[this] class ValuesIterator[B](tree: Tree[B]) extends TreeIterator[B, B](tree) {
    override def nextResult(tree: Tree[B]) = tree.value
  }
}

	