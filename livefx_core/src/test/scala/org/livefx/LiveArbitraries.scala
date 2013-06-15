package org.livefx

import scalaz.Show, scalaz.syntax.show._
import scalaz.scalacheck.ScalazArbitrary._
import org.scalacheck.{Pretty, Gen, Arbitrary}, Arbitrary.arbitrary, Gen.{frequency, oneOf}

trait LiveArbitraries {
  implicit val LiveIntArbitrary: Arbitrary[Live[Int]] = Arbitrary(arbitrary[Int] map (const(_)))

  implicit def LiveIntFunctionArbitrary: Arbitrary[Live[Int => Int]] = Arbitrary(Gen(params => None))
}

object LiveArbitraries extends LiveArbitraries
