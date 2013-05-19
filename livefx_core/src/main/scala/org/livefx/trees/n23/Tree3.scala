package org.livefx.trees.n23

case class Tree3[+A](l: Tree[A], v: A, c: Tree[A], w: A, r: Tree[A]) extends Tree[A] {
  final override def count: Int = 2
  final override val size: Int = l.size + 1 + c.size + 2 + r.size
}
