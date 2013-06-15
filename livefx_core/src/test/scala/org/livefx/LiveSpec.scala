package org.livefx

import scalaz._
import Scalaz._
import effect._
import scalaz.scalacheck.ScalazProperties._
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.Suite

class LiveSpec extends Spec {
  "Live" should {
    "satisfy monad laws" ! monad.laws[Live]
  }

  implicit def LiveIntEqual: Equal[Live[Int]] = Equal.equal((a, b) => a.value == b.value)
}

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(classOf[LiveSpec]))
object LiveSpec extends LiveSpec
