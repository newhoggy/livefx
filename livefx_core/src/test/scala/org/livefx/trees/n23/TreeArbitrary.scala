package org.livefx.trees.n23

import org.scalacheck.Arbitrary, Arbitrary._

object TreeArbitrary {
  implicit def ArbitraryTree[A](implicit A: Arbitrary[A]): Arbitrary[Tree[A]] =
    Arbitrary(arbitrary[List[A]] map {
      case (l) => Tree(l)
    })
}
