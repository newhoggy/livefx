package org.livefx.volume

import scala.annotation.tailrec
import scalaz.Monoid
import scala.annotation.meta.getter

sealed abstract class VolumeTree[+B] extends Serializable {
  def value: B
  def left: VolumeTree[B]
  def right: VolumeTree[B]
  def black: VolumeTree[B]
  def red: VolumeTree[B]
  def count: Int
}

final case object VolumeLeaf extends VolumeTree[Nothing] {
  def value: Nothing = throw new UnsupportedOperationException
  def left: VolumeTree[Nothing] = throw new UnsupportedOperationException
  def right: VolumeTree[Nothing] = throw new UnsupportedOperationException
  def count: Int = 0
  def black: VolumeTree[Nothing] = throw new UnsupportedOperationException
  def red: VolumeTree[Nothing] = throw new UnsupportedOperationException
}

object VolumeTree {
  def get[B](tree: VolumeTree[B], index: Int): Option[B] = lookup(tree, index) match {
    case VolumeLeaf => None
    case tree => Some(tree.value)
  }

  @tailrec
  def lookup[B](tree: VolumeTree[B], index: Int): VolumeTree[B] = if (tree == VolumeLeaf) VolumeLeaf else {
    if (index < tree.left.count) lookup(tree.left, index)
    else if (index > tree.left.count) lookup(tree.right, index)
    else tree
  }

  def update[B, B1 >: B](tree: VolumeTree[B], index: Int, v: B1, overwrite: Boolean): VolumeTree[B1] = blacken(upd(tree, index, v, overwrite))
  def insert[B, B1 >: B](tree: VolumeTree[B], index: Int, v: B1): VolumeTree[B1] = blacken(ins(tree, index, v))
  def delete[B](tree: VolumeTree[B], index: Int): VolumeTree[B] = blacken(del(tree, index))
  def rangeImpl[B](tree: VolumeTree[B], from: Option[Int], until: Option[Int]): VolumeTree[B] = (from, until) match {
    case (Some(from), Some(until)) => this.range(tree, from, until)
    case (Some(from), None)        => this.from(tree, from)
    case (None,       Some(until)) => this.until(tree, until)
    case (None,       None)        => tree
  }
  def range[B](tree: VolumeTree[B], from: Int, until: Int): VolumeTree[B] = blacken(doRange(tree, from, until))
  def from[B](tree: VolumeTree[B], from: Int): VolumeTree[B] = blacken(doFrom(tree, from))
  def to[B](tree: VolumeTree[B], to: Int): VolumeTree[B] = blacken(doTo(tree, to))
  def until[B](tree: VolumeTree[B], key: Int): VolumeTree[B] = blacken(doUntil(tree, key))

  def drop[B](tree: VolumeTree[B], n: Int): VolumeTree[B] = blacken(doDrop(tree, n))
  def take[B](tree: VolumeTree[B], n: Int): VolumeTree[B] = blacken(doTake(tree, n))
  def slice[B](tree: VolumeTree[B], from: Int, until: Int): VolumeTree[B] = blacken(doSlice(tree, from, until))

  def smallest[B](tree: VolumeTree[B]): VolumeTree[B] = {
    if (tree == VolumeLeaf) throw new NoSuchElementException("empty map")
    var result = tree
    while (result.left != VolumeLeaf) result = result.left
    result
  }
  def greatest[B](tree: VolumeTree[B]): VolumeTree[B] = {
    if (tree == VolumeLeaf) throw new NoSuchElementException("empty map")
    var result = tree
    while (result.right != VolumeLeaf) result = result.right
    result
  }

  def foreach[B, U](tree: VolumeTree[B], f: B => U): Unit = if (tree != VolumeLeaf) {
    if (tree.left != VolumeLeaf) foreach(tree.left, f)
    f(tree.value)
    if (tree.right != VolumeLeaf) foreach(tree.right, f)
  }

  def iterator[_, B](tree: VolumeTree[B]): Iterator[B] = new ValuesIterator(tree)

  @tailrec
  def nth[B](tree: VolumeTree[B], n: Int): VolumeTree[B] = {
    val count = tree.left.count
    if (n < count) nth(tree.left, n)
    else if (n > count) nth(tree.right, n - count - 1)
    else tree
  }

  def isBlack(tree: VolumeTree[_]) = (tree == VolumeLeaf) || isBlackTree(tree)

  private[this] def isRedTree(tree: VolumeTree[_]) = tree.isInstanceOf[RedTree[_]]
  private[this] def isBlackTree(tree: VolumeTree[_]) = tree.isInstanceOf[BlackTree[_]]

  private[this] def blacken[B](t: VolumeTree[B]): VolumeTree[B] = if (t == VolumeLeaf) VolumeLeaf else t.black

  private[this] def mkTree[B](isBlack: Boolean, v: B, l: VolumeTree[B], r: VolumeTree[B]) =
    if (isBlack) BlackTree(l, v, r) else RedTree(l, v, r)

  private[this] def balanceLeft[B, B1 >: B](isBlack: Boolean, zv: B, l: VolumeTree[B1], d: VolumeTree[B1]): VolumeTree[B1] = {
    if (isRedTree(l) && isRedTree(l.left))
      RedTree(BlackTree(l.left.left, l.left.value, l.left.right), l.value, BlackTree(l.right, zv, d))
    else if (isRedTree(l) && isRedTree(l.right))
      RedTree(BlackTree(l.left, l.value, l.right.left), l.right.value, BlackTree(l.right.right, zv, d))
    else
      mkTree(isBlack, zv, l, d)
  }
  private[this] def balanceRight[B, B1 >: B](isBlack: Boolean, xv: B, a: VolumeTree[B1], r: VolumeTree[B1]): VolumeTree[B1] = {
    if (isRedTree(r) && isRedTree(r.left))
      RedTree(BlackTree(a, xv, r.left.left), r.left.value, BlackTree(r.left.right, r.value, r.right))
    else if (isRedTree(r) && isRedTree(r.right))
      RedTree(BlackTree(a, xv, r.left), r.value, BlackTree(r.right.left, r.right.value, r.right.right))
    else
      mkTree(isBlack, xv, a, r)
  }
  private[this] def ins[B, B1 >: B](tree: VolumeTree[B], index: Int, v: B1): VolumeTree[B1] = {
    if (tree == VolumeLeaf) {
      RedTree(VolumeLeaf, v, VolumeLeaf)
    } else {
      if (index <= tree.left.count) {
        balanceLeft(isBlackTree(tree), tree.value, ins(tree.left, index, v), tree.right)
      } else if (index > tree.left.count) {
        balanceRight(isBlackTree(tree), tree.value, tree.left, ins(tree.right, index - tree.left.count - 1, v))
      } else {
        mkTree(isBlackTree(tree), v, tree.left, tree.right)
      }
    }
  }
  private[this] def upd[B, B1 >: B](tree: VolumeTree[B], index: Int, v: B1, overwrite: Boolean): VolumeTree[B1] = if (tree == VolumeLeaf) {
//    RedTree(v, VolumeLeaf, VolumeLeaf)
    throw new IndexOutOfBoundsException
  } else {
    if (index < tree.left.count) balanceLeft(isBlackTree(tree), tree.value, upd(tree.left, index, v, overwrite), tree.right)
    else if (index > tree.left.count) balanceRight(isBlackTree(tree), tree.value, tree.left, upd(tree.right, index - tree.left.count - 1, v, overwrite))
    else if (overwrite) mkTree(isBlackTree(tree), v, tree.left, tree.right)
    else tree
  }
  private[this] def updNth[B, B1 >: B](tree: VolumeTree[B], idx: Int, index: Int, v: B1, overwrite: Boolean): VolumeTree[B1] = if (tree == VolumeLeaf) {
    RedTree(VolumeLeaf, v, VolumeLeaf)
  } else {
    val rank = tree.left.count + 1
    if (idx < rank) balanceLeft(isBlackTree(tree), tree.value, updNth(tree.left, idx, index, v, overwrite), tree.right)
    else if (idx > rank) balanceRight(isBlackTree(tree), tree.value, tree.left, updNth(tree.right, idx - rank, index, v, overwrite))
    else if (overwrite) mkTree(isBlackTree(tree), v, tree.left, tree.right)
    else tree
  }

  /* Based on Stefan Kahrs' Haskell version of Okasaki's Red&Black Trees
   * http://www.cse.unsw.edu.au/~dons/data/RedBlackTree.html */
  private[this] def del[B](tree: VolumeTree[B], index: Int): VolumeTree[B] = if (tree == VolumeLeaf) VolumeLeaf else {
    def balance(xv: B, tl: VolumeTree[B], tr: VolumeTree[B]) = if (isRedTree(tl)) {
      if (isRedTree(tr)) {
        RedTree(tl.black, xv, tr.black)
      } else if (isRedTree(tl.left)) {
        RedTree(tl.left.black, tl.value, BlackTree(tl.right, xv, tr))
      } else if (isRedTree(tl.right)) {
        RedTree(BlackTree(tl.left, tl.value, tl.right.left), tl.right.value, BlackTree(tl.right.right, xv, tr))
      } else {
        BlackTree(tl, xv, tr)
      }
    } else if (isRedTree(tr)) {
      if (isRedTree(tr.right)) {
        RedTree(BlackTree(tl, xv, tr.left), tr.value, tr.right.black)
      } else if (isRedTree(tr.left)) {
        RedTree(BlackTree(tl, xv, tr.left.left), tr.left.value, BlackTree(tr.left.right, tr.value, tr.right))
      } else {
        BlackTree(tl, xv, tr)
      }
    } else {
      BlackTree(tl, xv, tr)
    }
    def subl(t: VolumeTree[B]) =
      if (t.isInstanceOf[BlackTree[_]]) t.red
      else sys.error("Defect: invariance violation; expected black, got "+t)

    def balLeft(xv: B, tl: VolumeTree[B], tr: VolumeTree[B]) = if (isRedTree(tl)) {
      RedTree(tl.black, xv, tr)
    } else if (isBlackTree(tr)) {
      balance(xv, tl, tr.red)
    } else if (isRedTree(tr) && isBlackTree(tr.left)) {
      RedTree(BlackTree(tl, xv, tr.left.left), tr.left.value, balance(tr.value, tr.left.right, subl(tr.right)))
    } else {
      sys.error("Defect: invariance violation")
    }
    def balRight(xv: B, tl: VolumeTree[B], tr: VolumeTree[B]) = if (isRedTree(tr)) {
      RedTree(tl, xv, tr.black)
    } else if (isBlackTree(tl)) {
      balance(xv, tl.red, tr)
    } else if (isRedTree(tl) && isBlackTree(tl.right)) {
      RedTree(balance(tl.value, subl(tl.left), tl.right.left), tl.right.value, BlackTree(tl.right.right, xv, tr))
    } else {
      sys.error("Defect: invariance violation")
    }
    def delLeft = if (isBlackTree(tree.left)) balLeft(tree.value, del(tree.left, index), tree.right) else RedTree(del(tree.left, index), tree.value, tree.right)
    def delRight = if (isBlackTree(tree.right)) balRight(tree.value, tree.left, del(tree.right, index - tree.left.count - 1)) else RedTree(tree.left, tree.value, del(tree.right, index - tree.left.count - 1))
    def append(tl: VolumeTree[B], tr: VolumeTree[B]): VolumeTree[B] = if (tl == VolumeLeaf) {
      tr
    } else if (tr == VolumeLeaf) {
      tl
    } else if (isRedTree(tl) && isRedTree(tr)) {
      val bc = append(tl.right, tr.left)
      if (isRedTree(bc)) {
        RedTree(RedTree(tl.left, tl.value, bc.left), bc.value, RedTree(bc.right, tr.value, tr.right))
      } else {
        RedTree(tl.left, tl.value, RedTree(bc, tr.value, tr.right))
      }
    } else if (isBlackTree(tl) && isBlackTree(tr)) {
      val bc = append(tl.right, tr.left)
      if (isRedTree(bc)) {
        RedTree(BlackTree(tl.left, tl.value, bc.left), bc.value, BlackTree(bc.right, tr.value, tr.right))
      } else {
        balLeft(tl.value, tl.left, BlackTree(bc, tr.value, tr.right))
      }
    } else if (isRedTree(tr)) {
      RedTree(append(tl, tr.left), tr.value, tr.right)
    } else if (isRedTree(tl)) {
      RedTree(tl.left, tl.value, append(tl.right, tr))
    } else {
      sys.error("unmatched tree on append: " + tl + ", " + tr)
    }

    if (index < tree.left.count) delLeft
    else if (index > tree.left.count) delRight
    else append(tree.left, tree.right)
  }

  private[this] def doFrom[B](tree: VolumeTree[B], from: Int): VolumeTree[B] = {
    if (tree == VolumeLeaf) return VolumeLeaf
    if (tree.left.count < from) return doFrom(tree.right, from - tree.left.count - 1)
    val newLeft = doFrom(tree.left, from)
    if (newLeft eq tree.left) tree
    else if (newLeft == VolumeLeaf) upd(tree.right, 0, tree.value, false)
    else rebalance(tree, newLeft, tree.right)
  }
  private[this] def doTo[B](tree: VolumeTree[B], to: Int): VolumeTree[B] = {
    if (tree == VolumeLeaf) return VolumeLeaf
    if (to < tree.left.count) return doTo(tree.left, to)
    val newRight = doTo(tree.right, to)
    if (newRight eq tree.right) tree
    else if (newRight == VolumeLeaf) upd(tree.left, 0, tree.value, false)
    else rebalance(tree, tree.left, newRight)
  }
  private[this] def doUntil[B](tree: VolumeTree[B], until: Int): VolumeTree[B] = {
    if (tree == VolumeLeaf) return VolumeLeaf
    if (until <= tree.left.count) return doUntil(tree.left, until)
    val newRight = doUntil(tree.right, until)
    if (newRight eq tree.right) tree
    else if (newRight == VolumeLeaf) upd(tree.left, 0, tree.value, false)
    else rebalance(tree, tree.left, newRight)
  }
  private[this] def doRange[B](tree: VolumeTree[B], from: Int, until: Int): VolumeTree[B] = {
    if (tree == VolumeLeaf) return VolumeLeaf
    if (tree.left.count < from) return doRange(tree.right, from - tree.left.count - 1, until - tree.left.count - 1)
    if (until <= tree.left.count) return doRange(tree.left, from, until)
    val newLeft = doFrom(tree.left, from)
    val newRight = doUntil(tree.right, until - tree.left.count - 1)
    if ((newLeft eq tree.left) && (newRight eq tree.right)) tree
    else if (newLeft == VolumeLeaf) upd(newRight, 0, tree.value, false)
    else if (newRight == VolumeLeaf) upd(newLeft, 0, tree.value, false)
    else rebalance(tree, newLeft, newRight)
  }

  private[this] def doDrop[B](tree: VolumeTree[B], n: Int): VolumeTree[B] = {
    if (n <= 0) return tree
    if (n >= tree.count) return VolumeLeaf
    val count = tree.left.count
    if (n > count) return doDrop(tree.right, n - count - 1)
    val newLeft = doDrop(tree.left, n)
    if (newLeft eq tree.left) tree
    else if (newLeft == VolumeLeaf) updNth(tree.right, n - count - 1, 0, tree.value, false)
    else rebalance(tree, newLeft, tree.right)
  }
  private[this] def doTake[B](tree: VolumeTree[B], n: Int): VolumeTree[B] = {
    if (n <= 0) return VolumeLeaf
    if (n >= tree.count) return tree
    val count = tree.left.count
    if (n <= count) return doTake(tree.left, n)
    val newRight = doTake(tree.right, n - count - 1)
    if (newRight eq tree.right) tree
    else if (newRight == VolumeLeaf) updNth(tree.left, n, 0, tree.value, false)
    else rebalance(tree, tree.left, newRight)
  }
  private[this] def doSlice[B](tree: VolumeTree[B], from: Int, until: Int): VolumeTree[B] = {
    if (tree == VolumeLeaf) return VolumeLeaf
    val count = tree.left.count
    if (from > count) return doSlice(tree.right, from - count - 1, until - count - 1)
    if (until <= count) return doSlice(tree.left, from, until)
    val newLeft = doDrop(tree.left, from)
    val newRight = doTake(tree.right, until - count - 1)
    if ((newLeft eq tree.left) && (newRight eq tree.right)) tree
    else if (newLeft == VolumeLeaf) updNth(newRight, from - count - 1, 0, tree.value, false)
    else if (newRight == VolumeLeaf) updNth(newLeft, until, 0, tree.value, false)
    else rebalance(tree, newLeft, newRight)
  }

  // The zipper returned might have been traversed left-most (always the left child)
  // or right-most (always the right child). Left trees are traversed right-most,
  // and right trees are traversed leftmost.

  // Returns the zipper for the side with deepest black nodes depth, a flag
  // indicating whether the trees were unbalanced at all, and a flag indicating
  // whether the zipper was traversed left-most or right-most.

  // If the trees were balanced, returns an empty zipper
  private[this] def compareDepth[B](left: VolumeTree[B], right: VolumeTree[B]): (List[VolumeTree[B]], Boolean, Boolean, Int) = {
    // Once a side is found to be deeper, unzip it to the bottom
    def unzip(zipper: List[VolumeTree[B]], leftMost: Boolean): List[VolumeTree[B]] = {
      val next = if (leftMost) zipper.head.left else zipper.head.right
      next match {
        case VolumeLeaf => zipper
        case node => unzip(node :: zipper, leftMost)
      }
    }

    // Unzip left tree on the rightmost side and right tree on the leftmost side until one is
    // found to be deeper, or the bottom is reached
    def unzipBoth(left: VolumeTree[B],
                  right: VolumeTree[B],
                  leftZipper: List[VolumeTree[B]],
                  rightZipper: List[VolumeTree[B]],
                  smallerDepth: Int): (List[VolumeTree[B]], Boolean, Boolean, Int) = {
      if (isBlackTree(left) && isBlackTree(right)) {
        unzipBoth(left.right, right.left, left :: leftZipper, right :: rightZipper, smallerDepth + 1)
      } else if (isRedTree(left) && isRedTree(right)) {
        unzipBoth(left.right, right.left, left :: leftZipper, right :: rightZipper, smallerDepth)
      } else if (isRedTree(right)) {
        unzipBoth(left, right.left, leftZipper, right :: rightZipper, smallerDepth)
      } else if (isRedTree(left)) {
        unzipBoth(left.right, right, left :: leftZipper, rightZipper, smallerDepth)
      } else if ((left == VolumeLeaf) && (right == VolumeLeaf)) {
        (Nil, true, false, smallerDepth)
      } else if ((left == VolumeLeaf) && isBlackTree(right)) {
        val leftMost = true
        (unzip(right :: rightZipper, leftMost), false, leftMost, smallerDepth)
      } else if (isBlackTree(left) && (right == VolumeLeaf)) {
        val leftMost = false
        (unzip(left :: leftZipper, leftMost), false, leftMost, smallerDepth)
      } else {
        sys.error("unmatched trees in unzip: " + left + ", " + right)
      }
    }
    unzipBoth(left, right, Nil, Nil, 0)
  }

  private[this] def rebalance[B](tree: VolumeTree[B], newLeft: VolumeTree[B], newRight: VolumeTree[B]) = {
    // This is like drop(n-1), but only counting black nodes
    def  findDepth(zipper: List[VolumeTree[B]], depth: Int): List[VolumeTree[B]] = zipper match {
      case head :: tail if isBlackTree(head) =>
        if (depth == 1) zipper else findDepth(tail, depth - 1)
      case _ :: tail => findDepth(tail, depth)
      case Nil => sys.error("Defect: unexpected empty zipper while computing range")
    }

    // Blackening the smaller tree avoids balancing problems on union;
    // this can't be done later, though, or it would change the result of compareDepth
    val blkNewLeft = blacken(newLeft)
    val blkNewRight = blacken(newRight)
    val (zipper, levelled, leftMost, smallerDepth) = compareDepth(blkNewLeft, blkNewRight)

    if (levelled) {
      BlackTree(blkNewLeft, tree.value, blkNewRight)
    } else {
      val zipFrom = findDepth(zipper, smallerDepth)
      val union = if (leftMost) {
        RedTree(blkNewLeft, tree.value, zipFrom.head)
      } else {
        RedTree(zipFrom.head, tree.value, blkNewRight)
      }
      val zippedTree = zipFrom.tail.foldLeft(union: VolumeTree[B]) { (tree, node) =>
        if (leftMost)
          balanceLeft(isBlackTree(node), node.value, tree, node.right)
        else
          balanceRight(isBlackTree(node), node.value, node.left, tree)
      }
      zippedTree
    }
  }

  final case class RedTree[+B](left: VolumeTree[B], value: B, right: VolumeTree[B]) extends VolumeTree[B] {
    override def black: VolumeTree[B] = BlackTree(left, value, right)
    override def red: VolumeTree[B] = this
    override def toString: String = "RedTree(" + value + ", " + left + ", " + right + ")"
    final val count: Int = 1 + left.count + right.count
  }
  
  final case class BlackTree[+B](left: VolumeTree[B], value: B, right: VolumeTree[B]) extends VolumeTree[B] {
    override def black: VolumeTree[B] = this
    override def red: VolumeTree[B] = RedTree(left, value, right)
    override def toString: String = "BlackTree(" + value + ", " + left + ", " + right + ")"
    final val count: Int = 1 + left.count + right.count
  }
  
  private[this] abstract class TreeIterator[B, R](tree: VolumeTree[B]) extends Iterator[R] {
    protected[this] def nextResult(tree: VolumeTree[B]): R

    override def hasNext: Boolean = next != VolumeLeaf

    override def next: R = next match {
      case VolumeLeaf =>
        throw new NoSuchElementException("next on empty iterator")
      case tree =>
        next = findNext(tree.right)
        nextResult(tree)
    }

    @tailrec
    private[this] def findNext(tree: VolumeTree[B]): VolumeTree[B] = {
      if (tree == VolumeLeaf) popPath()
      else if (tree.left == VolumeLeaf) tree
      else {
        pushPath(tree)
        findNext(tree.left)
      }
    }

    private[this] def pushPath(tree: VolumeTree[B]) {
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
          path :+= VolumeLeaf
          pushPath(tree)
      }
    }
    private[this] def popPath(): VolumeTree[B] = if (index == 0) VolumeLeaf else {
      index -= 1
      path(index)
    }

    private[this] var path = if (tree == VolumeLeaf) null else {
      /*
       * According to "Ralf Hinze. Constructing red-black trees" [http://www.cs.ox.ac.uk/ralf.hinze/publications/#P5]
       * the maximum height of a red-black tree is 2*log_2(n + 2) - 2.
       *
       * According to {@see Integer#numberOfLeadingZeros} ceil(log_2(n)) = (32 - Integer.numberOfLeadingZeros(n - 1))
       *
       * We also don't store the deepest nodes in the path so the maximum path length is further reduced by one.
       */
      val maximumHeight = 2 * (32 - Integer.numberOfLeadingZeros(tree.count + 2 - 1)) - 2 - 1
      new Array[VolumeTree[B]](maximumHeight)
    }
    private[this] var index = 0
    private[this] var next: VolumeTree[B] = findNext(tree)
  }

  private[this] class ValuesIterator[B](tree: VolumeTree[B]) extends TreeIterator[B, B](tree) {
    override def nextResult(tree: VolumeTree[B]) = tree.value
  }
}
