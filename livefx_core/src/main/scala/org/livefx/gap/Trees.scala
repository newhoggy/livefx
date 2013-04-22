package org.livefx.gap

import scala.annotation.tailrec
import org.livefx.debug._

abstract class Trees[+A] {
  def head: Tree[A]
  def tail: Trees[A]
  def isEmpty: Boolean
  def ::[B >: A](head: Tree[B]): Trees[B] = TreesCons(head, this)
  def prependReversed[B >: A](values: Trees[B]): Trees[B] = values match {
    case TreesCons(head, tail) => (head::this)
  }
  def treeCount: Int
  def size: Int
  def trees: List[Tree[A]]

  def drop(n: Int): Trees[A]
  def take(n: Int): Trees[A]
}

final case class TreesCons[+A](head: Tree[A], tail: Trees[A]) extends Trees[A] {
  override val treeCount: Int = tail.treeCount + 1
  override val size: Int = head.size + tail.size

  assert(size == trees.map(_.size).sum)
  
  override def isEmpty: Boolean = false
  override def trees: List[Tree[A]] = head::tail.trees
  
  override def drop(n: Int): Trees[A] = {
    if (n < 0) throw new IndexOutOfBoundsException
    if (n > 0) tail.drop(n - 1) else this 
  }
  
  override def take(n: Int): Trees[A] = {
    if (n < 0) throw new IndexOutOfBoundsException
    if (n > 0) head::tail.take(n - 1) else TreesNil
  }
}

final object TreesNil extends Trees[Nothing] {
  override def head: Tree[Nothing] = throw new UnsupportedOperationException
  override def tail: Trees[Nothing] = throw new UnsupportedOperationException
  override def isEmpty: Boolean = true
  override def treeCount: Int = 0
  override def size: Int = 0
  override def trees: List[Tree[Nothing]] = Nil
  override def drop(n: Int): Trees[Nothing] = if (n == 0) this else throw new IndexOutOfBoundsException
  override def take(n: Int): Trees[Nothing] = if (n == 0) this else throw new IndexOutOfBoundsException
  override def toString: String = "TreesNil"
}

object Trees {
  
}
