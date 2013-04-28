package org.livefx.gap

final case class Root[+A](child: Tree[A] = Leaf0) {
  final def size: Int = child.size
  final def count: Int = 0
  final def toList[B >: A](acc: List[B]): List[B] = child.toList(acc)

  final def insert[B >: A](index: Int, value: B): Root[B] = child.insert(index, value) match {
    case Branch4(a, b, c, d) => Root(Branch2(Branch2(a, b), Branch2(c, d)))
    case Leaf4(a, b, c, d) => Root(Branch2(Leaf2(a, b), Leaf2(c, d)))
    case newChild => Root(newChild)
  }
}
