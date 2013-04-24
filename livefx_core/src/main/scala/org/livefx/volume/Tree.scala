package org.livefx.volume

import scala.annotation.tailrec
import scalaz.Monoid
import scala.annotation.meta.getter

object RedBlackTree {
  def get[B](tree: Tree[B], index: Int): Option[B] = lookup(tree, index) match {
    case Leaf => None
    case tree => Some(tree.value)
  }

  @tailrec
  def lookup[B](tree: Tree[B], index: Int): Tree[B] = if (tree == Leaf) Leaf else {
    if (index < tree.left.count) lookup(tree.left, index)
    else if (index > tree.left.count) lookup(tree.right, index)
    else tree
  }

  def update[B, B1 >: B](tree: Tree[B], index: Int, v: B1, overwrite: Boolean): Tree[B1] = blacken(upd(tree, index, v, overwrite))
  def insert[B, B1 >: B](tree: Tree[B], index: Int, v: B1): Tree[B1] = blacken(ins(tree, index, v))
  def delete[B](tree: Tree[B], index: Int): Tree[B] = blacken(del(tree, index))
  def rangeImpl[B](tree: Tree[B], from: Option[Int], until: Option[Int]): Tree[B] = (from, until) match {
    case (Some(from), Some(until)) => this.range(tree, from, until)
    case (Some(from), None)        => this.from(tree, from)
    case (None,       Some(until)) => this.until(tree, until)
    case (None,       None)        => tree
  }
  def range[B](tree: Tree[B], from: Int, until: Int): Tree[B] = blacken(doRange(tree, from, until))
  def from[B](tree: Tree[B], from: Int): Tree[B] = blacken(doFrom(tree, from))
  def to[B](tree: Tree[B], to: Int): Tree[B] = blacken(doTo(tree, to))
  def until[B](tree: Tree[B], key: Int): Tree[B] = blacken(doUntil(tree, key))

  def drop[B](tree: Tree[B], n: Int): Tree[B] = blacken(doDrop(tree, n))
  def take[B](tree: Tree[B], n: Int): Tree[B] = blacken(doTake(tree, n))
  def slice[B](tree: Tree[B], from: Int, until: Int): Tree[B] = blacken(doSlice(tree, from, until))

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

  @tailrec
  def nth[B](tree: Tree[B], n: Int): Tree[B] = {
    val count = tree.left.count
    if (n < count) nth(tree.left, n)
    else if (n > count) nth(tree.right, n - count - 1)
    else tree
  }

  def isBlack(tree: Tree[_]) = (tree == Leaf) || isBlackTree(tree)

  private[this] def isRedTree(tree: Tree[_]) = tree.isInstanceOf[RedTree[_]]
  private[this] def isBlackTree(tree: Tree[_]) = tree.isInstanceOf[BlackTree[_]]

  private[this] def blacken[B](t: Tree[B]): Tree[B] = if (t == Leaf) Leaf else t.black

  private[this] def mkTree[B](isBlack: Boolean, v: B, l: Tree[B], r: Tree[B]) =
    if (isBlack) BlackTree(l, v, r) else RedTree(l, v, r)

  private[this] def balanceLeft[B, B1 >: B](isBlack: Boolean, zv: B, l: Tree[B1], d: Tree[B1]): Tree[B1] = {
    if (isRedTree(l) && isRedTree(l.left))
      RedTree(BlackTree(l.left.left, l.left.value, l.left.right), l.value, BlackTree(l.right, zv, d))
    else if (isRedTree(l) && isRedTree(l.right))
      RedTree(BlackTree(l.left, l.value, l.right.left), l.right.value, BlackTree(l.right.right, zv, d))
    else
      mkTree(isBlack, zv, l, d)
  }
  private[this] def balanceRight[B, B1 >: B](isBlack: Boolean, xv: B, a: Tree[B1], r: Tree[B1]): Tree[B1] = {
    if (isRedTree(r) && isRedTree(r.left))
      RedTree(BlackTree(a, xv, r.left.left), r.left.value, BlackTree(r.left.right, r.value, r.right))
    else if (isRedTree(r) && isRedTree(r.right))
      RedTree(BlackTree(a, xv, r.left), r.value, BlackTree(r.right.left, r.right.value, r.right.right))
    else
      mkTree(isBlack, xv, a, r)
  }
  private[this] def ins[B, B1 >: B](tree: Tree[B], index: Int, v: B1): Tree[B1] = {
    if (tree == Leaf) {
      RedTree(Leaf, v, Leaf)
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
  private[this] def upd[B, B1 >: B](tree: Tree[B], index: Int, v: B1, overwrite: Boolean): Tree[B1] = if (tree == Leaf) {
//    RedTree(v, Leaf, Leaf)
    throw new IndexOutOfBoundsException
  } else {
    if (index < tree.left.count) balanceLeft(isBlackTree(tree), tree.value, upd(tree.left, index, v, overwrite), tree.right)
    else if (index > tree.left.count) balanceRight(isBlackTree(tree), tree.value, tree.left, upd(tree.right, index - tree.left.count - 1, v, overwrite))
    else if (overwrite) mkTree(isBlackTree(tree), v, tree.left, tree.right)
    else tree
  }
  private[this] def updNth[B, B1 >: B](tree: Tree[B], idx: Int, index: Int, v: B1, overwrite: Boolean): Tree[B1] = if (tree == Leaf) {
    RedTree(Leaf, v, Leaf)
  } else {
    val rank = tree.left.count + 1
    if (idx < rank) balanceLeft(isBlackTree(tree), tree.value, updNth(tree.left, idx, index, v, overwrite), tree.right)
    else if (idx > rank) balanceRight(isBlackTree(tree), tree.value, tree.left, updNth(tree.right, idx - rank, index, v, overwrite))
    else if (overwrite) mkTree(isBlackTree(tree), v, tree.left, tree.right)
    else tree
  }

  /* Based on Stefan Kahrs' Haskell version of Okasaki's Red&Black Trees
   * http://www.cse.unsw.edu.au/~dons/data/RedBlackTree.html */
  private[this] def del[B](tree: Tree[B], index: Int): Tree[B] = if (tree == Leaf) Leaf else {
    def balance(xv: B, tl: Tree[B], tr: Tree[B]) = if (isRedTree(tl)) {
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
    def subl(t: Tree[B]) =
      if (t.isInstanceOf[BlackTree[_]]) t.red
      else sys.error("Defect: invariance violation; expected black, got "+t)

    def balLeft(xv: B, tl: Tree[B], tr: Tree[B]) = if (isRedTree(tl)) {
      RedTree(tl.black, xv, tr)
    } else if (isBlackTree(tr)) {
      balance(xv, tl, tr.red)
    } else if (isRedTree(tr) && isBlackTree(tr.left)) {
      RedTree(BlackTree(tl, xv, tr.left.left), tr.left.value, balance(tr.value, tr.left.right, subl(tr.right)))
    } else {
      sys.error("Defect: invariance violation")
    }
    def balRight(xv: B, tl: Tree[B], tr: Tree[B]) = if (isRedTree(tr)) {
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
    def append(tl: Tree[B], tr: Tree[B]): Tree[B] = if (tl == Leaf) {
      tr
    } else if (tr == Leaf) {
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

  private[this] def doFrom[B](tree: Tree[B], from: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    if (tree.left.count < from) return doFrom(tree.right, from - tree.left.count - 1)
    val newLeft = doFrom(tree.left, from)
    if (newLeft eq tree.left) tree
    else if (newLeft == Leaf) upd(tree.right, 0, tree.value, false)
    else rebalance(tree, newLeft, tree.right)
  }
  private[this] def doTo[B](tree: Tree[B], to: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    if (to < tree.left.count) return doTo(tree.left, to)
    val newRight = doTo(tree.right, to)
    if (newRight eq tree.right) tree
    else if (newRight == Leaf) upd(tree.left, 0, tree.value, false)
    else rebalance(tree, tree.left, newRight)
  }
  private[this] def doUntil[B](tree: Tree[B], until: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    if (until <= tree.left.count) return doUntil(tree.left, until)
    val newRight = doUntil(tree.right, until)
    if (newRight eq tree.right) tree
    else if (newRight == Leaf) upd(tree.left, 0, tree.value, false)
    else rebalance(tree, tree.left, newRight)
  }
  private[this] def doRange[B](tree: Tree[B], from: Int, until: Int): Tree[B] = {
    if (tree == Leaf) return Leaf
    if (tree.left.count < from) return doRange(tree.right, from - tree.left.count - 1, until - tree.left.count - 1)
    if (until <= tree.left.count) return doRange(tree.left, from, until)
    val newLeft = doFrom(tree.left, from)
    val newRight = doUntil(tree.right, until - tree.left.count - 1)
    if ((newLeft eq tree.left) && (newRight eq tree.right)) tree
    else if (newLeft == Leaf) upd(newRight, 0, tree.value, false)
    else if (newRight == Leaf) upd(newLeft, 0, tree.value, false)
    else rebalance(tree, newLeft, newRight)
  }

  private[this] def doDrop[B](tree: Tree[B], n: Int): Tree[B] = {
    if (n <= 0) return tree
    if (n >= tree.count) return Leaf
    val count = tree.left.count
    if (n > count) return doDrop(tree.right, n - count - 1)
    val newLeft = doDrop(tree.left, n)
    if (newLeft eq tree.left) tree
    else if (newLeft == Leaf) updNth(tree.right, n - count - 1, 0, tree.value, false)
    else rebalance(tree, newLeft, tree.right)
  }
  private[this] def doTake[B](tree: Tree[B], n: Int): Tree[B] = {
    if (n <= 0) return Leaf
    if (n >= tree.count) return tree
    val count = tree.left.count
    if (n <= count) return doTake(tree.left, n)
    val newRight = doTake(tree.right, n - count - 1)
    if (newRight eq tree.right) tree
    else if (newRight == Leaf) updNth(tree.left, n, 0, tree.value, false)
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
    else if (newLeft == Leaf) updNth(newRight, from - count - 1, 0, tree.value, false)
    else if (newRight == Leaf) updNth(newLeft, until, 0, tree.value, false)
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
      if (isBlackTree(left) && isBlackTree(right)) {
        unzipBoth(left.right, right.left, left :: leftZipper, right :: rightZipper, smallerDepth + 1)
      } else if (isRedTree(left) && isRedTree(right)) {
        unzipBoth(left.right, right.left, left :: leftZipper, right :: rightZipper, smallerDepth)
      } else if (isRedTree(right)) {
        unzipBoth(left, right.left, leftZipper, right :: rightZipper, smallerDepth)
      } else if (isRedTree(left)) {
        unzipBoth(left.right, right, left :: leftZipper, rightZipper, smallerDepth)
      } else if ((left == Leaf) && (right == Leaf)) {
        (Nil, true, false, smallerDepth)
      } else if ((left == Leaf) && isBlackTree(right)) {
        val leftMost = true
        (unzip(right :: rightZipper, leftMost), false, leftMost, smallerDepth)
      } else if (isBlackTree(left) && (right == Leaf)) {
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
      val zippedTree = zipFrom.tail.foldLeft(union: Tree[B]) { (tree, node) =>
        if (leftMost)
          balanceLeft(isBlackTree(node), node.value, tree, node.right)
        else
          balanceRight(isBlackTree(node), node.value, node.left, tree)
      }
      zippedTree
    }
  }

  sealed abstract class Tree[+B] extends Serializable {
    def value: B
    def left: Tree[B]
    def right: Tree[B]
    def black: Tree[B]
    def red: Tree[B]
    def count: Int
  }

  final case class RedTree[+B](left: Tree[B], value: B, right: Tree[B]) extends Tree[B] {
    override def black: Tree[B] = BlackTree(left, value, right)
    override def red: Tree[B] = this
    override def toString: String = "RedTree(" + value + ", " + left + ", " + right + ")"
    final val count: Int = 1 + left.count + right.count
  }
  
  final case class BlackTree[+B](left: Tree[B], value: B, right: Tree[B]) extends Tree[B] {
    override def black: Tree[B] = this
    override def red: Tree[B] = RedTree(left, value, right)
    override def toString: String = "BlackTree(" + value + ", " + left + ", " + right + ")"
    final val count: Int = 1 + left.count + right.count
  }
  
  final case object Leaf extends Tree[Nothing] {
    def value: Nothing = throw new UnsupportedOperationException
    def left: Tree[Nothing] = throw new UnsupportedOperationException
    def right: Tree[Nothing] = throw new UnsupportedOperationException
    def count: Int = 0
    def black: Tree[Nothing] = throw new UnsupportedOperationException
    def red: Tree[Nothing] = throw new UnsupportedOperationException
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
