package org.livefx.gap

abstract class Trees[+A] {
  def head: Tree[A]
  def tail: Trees[A]
}

final case class TreesCons[+A](head: Tree[A], tail: Trees[A]) extends Trees[A] {
  def ::[B >: A](head: Tree[B]): Trees[B] = TreesCons(head, this)
}

final object TreesNil extends Trees[Nothing] {
  override def head: Tree[Nothing] = throw new UnsupportedOperationException
  override def tail: Trees[Nothing] = throw new UnsupportedOperationException
}
