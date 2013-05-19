package org.livefx.trees.n23

case class Tree2[+A](l: Tree[A], v: A, r: Tree[A]) extends Tree[A] {
  final override def count: Int = 1
  final override val size: Int = l.size + 1 + r.size
}
