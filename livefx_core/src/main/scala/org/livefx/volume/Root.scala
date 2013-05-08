package org.livefx.volume

import scalaz.Monoid

final case class Root[+A](child: Tree[A, Int] = Leaf0[Int]())(implicit hm: HasMonoid[A, Int]) {
  final def size: Int = child.size
  final def count: Int = 0
  final def toList[B >: A](acc: List[B]): List[B] = child.toList(acc)

  final def insert[B >: A](index: Int, value: B)(implicit hm: HasMonoid[B, Int]): Root[B] = Root(child.insert(index, value) match {
    case Branch4(a, b, c, d) => Branch2(Branch2(a, b), Branch2(c, d))
    case Leaf4(a, b, c, d) => Branch2(Leaf2(a, b), Leaf2(c, d))
    case newChild => newChild
  })

  final def update[B >: A](index: Int, value: B)(implicit hm: HasMonoid[B, Int]): Root[B] = Root(child.update(index, value))
  final def remove(index: Int): (A, Root[A]) = child.remove(index) match { case (value, nc) => (value, Root(nc)) }
}
