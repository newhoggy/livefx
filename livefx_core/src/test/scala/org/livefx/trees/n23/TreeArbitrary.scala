package org.livefx.trees.n23

import org.scalacheck.Arbitrary, Arbitrary._

object TreeArbitrary {
  def ArbitraryTree2[A](implicit A: Arbitrary[A]): Arbitrary[Tree[A]] =
    Arbitrary(arbitrary[List[A]] map {
      case (l) => Tree(l)
    })

  implicit def ArbitraryTree[A](implicit A: Arbitrary[A]): Arbitrary[Tree[A]] =
    Arbitrary(arbitrary[List[(Int, A)]] map { l =>
      l.foldLeft(Tree[A]()) { case (t, (n, v)) =>
        val m = (n & 0x7ffffff) % (t.size + 1)
        t.insertAt(m, v)
      }
    })
}
