package org.livefx.volume

import scalaz.Monoid

final case class Root[+A, M: Monoid](child: Tree[A, M])(implicit hm: HasMonoid[A, Int]) {
  final def size: Int = child.size
  final def count: Int = 0
  final def toList[B >: A](acc: List[B]): List[B] = child.toList(acc)

  final def insert[B >: A](index: Int, value: B)(implicit hm: HasMonoid[B, Int]): Root[B, M] = Root(child.insert(index, value) match {
    case Branch4(a, b, c, d) => Branch2(Branch2(a, b), Branch2(c, d))
    case Leaf4(a, b, c, d) => Branch2(Leaf2(a, b), Leaf2(c, d))
    case newChild => newChild
  })

  final def update[B >: A](index: Int, value: B)(implicit hm: HasMonoid[B, Int]): Root[B, M] = Root(child.update(index, value))
  final def remove(index: Int): (A, Root[A, M]) = child.remove(index) match { case (value, nc) => (value, Root(nc)) }
}

object Root {
  def apply[A, M: Monoid]()(implicit hm: HasMonoid[A, Int]): Root[A, M] = Root[A, M](Leaf0[M]())
}
