package org.livefx.trees.indexed

import scala.annotation.tailrec
import scala.Array.canBuildFrom

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

final case object Black extends Color

final case object Red extends Color

sealed abstract class Tree[+A] extends Serializable {
  val id = org.livefx.util.Memoize.objectId(this)
  def value: A
  def left: Tree[A]
  def right: Tree[A]
  def blacken: Tree[A]
  def redden: Tree[A]
  def size: Int
  def color: Color
  @inline final def toList: List[A] = Tree.toList(this)
  @inline final def map[C](f: A => C): Tree[C] = Tree.map(this, f)
  @inline final def mkString(l: String, d: String, r: String): String = this.toList.mkString(l, d, r)

  final def is(color: Color): Boolean = this.color == color

  @inline final def insert[B >: A](index: Int, v: B): Tree[B] = {
    def ins(tree: Tree[A], index: Int, v: B): Tree[B] = {
      if (tree == Leaf) {
        Red(Leaf, v, Leaf)
      } else {
        if (index <= tree.left.size) {
          tree.color.balanceLeft(ins(tree.left, index, v), tree.value, tree.right)
        } else if (index > tree.left.size) {
          tree.color.balanceRight(tree.left, tree.value, ins(tree.right, index - tree.left.size - 1, v))
        } else {
          tree.color(this.left, v, this.right)
        }
      }
    }
    
    ins(this, index, v).blacken
  }
  @inline final def update[B >: A](index: Int, v: B): Tree[B] = {
    def upd(tree: Tree[A], index: Int, v: B): Tree[B] = if (tree == Leaf) {
      throw new IndexOutOfBoundsException
    } else {
      if (index < tree.left.size) tree.color.balanceLeft(upd(tree.left, index, v), tree.value, tree.right)
      else if (index > tree.left.size) tree.color.balanceRight(tree.left, tree.value, upd(tree.right, index - tree.left.size - 1, v))
      else tree.color(tree.left, v, tree.right)
    }

    upd(this, index, v).blacken
  }
  @inline final def delete(index: Int): Tree[A] = this.del(index).blacken

  final def subl: Tree[A] = {
    if (this.isInstanceOf[Black[_]]) this.redden
    else sys.error("Defect: invariance violation; expected black, got " + this)
  }

  final def get(index: Int): Option[A] = this.lookup(index) match {
    case Leaf => None
    case tree => Some(tree.value)
  }

//  @tailrec
  final def lookup(index: Int): Tree[A] = {
    if (this == Leaf) Leaf
    else if (index < this.left.size) this.left.lookup(index)
    else if (index > this.left.size) this.right.lookup(index - this.left.size - 1)
    else this
  }

  /* Based on Stefan Kahrs' Haskell version of Okasaki's Red&Black Trees
   * http://www.cse.unsw.edu.au/~dons/data/RedBlackTree.html */
  private def del(index: Int): Tree[A] = if (this == Leaf) Leaf else {
    def balance(tl: Tree[A], xv: A, tr: Tree[A]) = if (tl.is(Red)) {
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

    def balLeft(tl: Tree[A], xv: A, tr: Tree[A]) = if (tl.is(Red)) {
      Red(tl.blacken, xv, tr)
    } else if (tr.is(Black)) {
      balance(tl, xv, tr.redden)
    } else if (tr.is(Red) && tr.left.is(Black)) {
      Red(Black(tl, xv, tr.left.left), tr.left.value, balance(tr.left.right, tr.value, tr.right.subl))
    } else {
      sys.error("Defect: invariance violation")
    }
    def balRight(tl: Tree[A], xv: A, tr: Tree[A]) = if (tr.is(Red)) {
      Red(tl, xv, tr.blacken)
    } else if (tl.is(Black)) {
      balance(tl.redden, xv, tr)
    } else if (tl.is(Red) && tl.right.is(Black)) {
      Red(balance(tl.left.subl, tl.value, tl.right.left), tl.right.value, Black(tl.right.right, xv, tr))
    } else {
      sys.error("Defect: invariance violation")
    }
    def delLeft = if (this.left.is(Black)) balLeft(this.left.del(index), this.value, this.right) else Red(this.left.del(index), this.value, this.right)
    def delRight = if (this.right.is(Black)) balRight(this.left, this.value, this.right.del(index - this.left.size - 1)) else Red(this.left, this.value, this.right.del(index - this.left.size - 1))
    def append(tl: Tree[A], tr: Tree[A]): Tree[A] = if (tl == Leaf) {
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

    if (index < this.left.size) delLeft
    else if (index > this.left.size) delRight
    else append(this.left, this.right)
  }
}

final case class Red[+A](left: Tree[A], value: A, right: Tree[A]) extends Tree[A] {
  override def blacken: Tree[A] = Black(left, value, right)
  override def redden: Tree[A] = this
  override def toString: String = "Red(" + value + ", " + left + ", " + right + ")"
  override def color: Color = Red
  final val size: Int = 1 + left.size + right.size
}

final case class Black[+A](left: Tree[A], value: A, right: Tree[A]) extends Tree[A] {
  override def blacken: Tree[A] = this
  override def redden: Tree[A] = Red(left, value, right)
  override def toString: String = "Black(" + value + ", " + left + ", " + right + ")"
  override def color: Color = Black
  final override val size: Int = 1 + left.size + right.size
}

final case object Leaf extends Tree[Nothing] {
  def value: Nothing = throw new UnsupportedOperationException
  def left: Tree[Nothing] = throw new UnsupportedOperationException
  def right: Tree[Nothing] = throw new UnsupportedOperationException
  def size: Int = 0
  def blacken: Tree[Nothing] = Leaf.this
  def redden: Tree[Nothing] = throw new UnsupportedOperationException
  override def color: Color = Black
}

object Tree {
  def idOf[A](tree: Tree[A]): Any = tree.id
  
  def foreach[A, U](tree: Tree[A], f: A => U): Unit = if (tree != Leaf) {
    if (tree.left != Leaf) foreach(tree.left, f)
    f(tree.value)
    if (tree.right != Leaf) foreach(tree.right, f)
  }

  def iterator[B](tree: Tree[B]): Iterator[B] = new ValuesIterator(tree)

  private[this] abstract class TreeIterator[A, R](tree: Tree[A]) extends Iterator[R] {
    protected[this] def nextResult(tree: Tree[A]): R

    override def hasNext: Boolean = next != Leaf

    override def next: R = next match {
      case Leaf =>
        throw new NoSuchElementException("next on empty iterator")
      case tree =>
        next = findNext(tree.right)
        nextResult(tree)
    }

    @tailrec
    private[this] def findNext(tree: Tree[A]): Tree[A] = {
      if (tree == Leaf) popPath()
      else if (tree.left == Leaf) tree
      else {
        pushPath(tree)
        findNext(tree.left)
      }
    }

    private[this] def pushPath(tree: Tree[A]) {
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
    private[this] def popPath(): Tree[A] = if (index == 0) Leaf else {
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
      val maximumHeight = 2 * (32 - Integer.numberOfLeadingZeros(tree.size + 2 - 1)) - 2 - 1
      new Array[Tree[A]](maximumHeight)
    }
    private[this] var index = 0
    private[this] var next: Tree[A] = findNext(tree)
  }

  private[this] class ValuesIterator[A](tree: Tree[A]) extends TreeIterator[A, A](tree) {
    override def nextResult(tree: Tree[A]) = tree.value
  }
  
  def unapply[A](t: Tree[A]): Option[(Tree[A], A, Tree[A])] = t match {
    case Black(l, v, r) => Some((l, v, r))
    case Red(l, v, r) => Some((l, v, r))
    case _ => None
  }
  
  def toList[A](t: Tree[A], acc: List[A] = Nil): List[A] = t match {
    case Tree(l, v, r) => toList(l, v::toList(r, acc))
    case Leaf => acc
  }

  def map[A, B](tree: Tree[A], f: A => B): Tree[B] = tree match {
    case t@Tree(Leaf, v, Leaf) => t.color(Leaf, f(v), Leaf)
    case t@Tree(l, v, Leaf) => t.color(map(l, f), f(v), Leaf)
    case t@Tree(Leaf, v, r) => t.color(Leaf, f(v), map(r, f))
    case t@Tree(l, v, r) => t.color(map(l, f), f(v), map(r, f))
    case Leaf => Leaf
  }
}
