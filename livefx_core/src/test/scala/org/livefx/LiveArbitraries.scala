package org.livefx

import scalaz.Show, scalaz.syntax.show._
import scalaz.scalacheck.ScalazArbitrary._
import org.scalacheck.{Pretty, Gen, Arbitrary}, Arbitrary.arbitrary, Gen.{frequency, oneOf}

trait LiveArbitraries {
  implicit val LiveIntArbitrary: Arbitrary[Live[Int]] = {
    println("--> live arbitary 1")
    Arbitrary(arbitrary[Int] map (const(_)))
  }

  implicit def LiveIntFunctionArbitrary: Arbitrary[Live[Int => Int]] = {
    println("--> live arbitary 2")
    Arbitrary(Gen(params => Some(const(identity[Int]))))
  }
}

object LiveArbitraries extends LiveArbitraries
