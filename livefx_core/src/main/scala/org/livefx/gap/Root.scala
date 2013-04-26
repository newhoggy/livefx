package org.livefx.gap

final case class Root[+A](child: Tree[A] = Leaf0) {
  final def size: Int = child.size
  final def count: Int = 0

  final def insert[B >: A](index: Int, value: B): Root[B] = Root(child.insert(index, value))
}
