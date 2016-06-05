package org.livefx.value

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

trait LiveArbitraries {
  implicit val LiveIntArbitrary: Arbitrary[Live[Int]] = Arbitrary(arbitrary[Int] map (Const(_)))
}

object LiveArbitraries extends LiveArbitraries
