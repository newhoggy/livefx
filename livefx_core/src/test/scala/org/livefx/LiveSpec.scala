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
    "satisfy monad laws rightIdentity" ! monad.rightIdentity[Live, Int]
    "satisfy monad laws leftIdentity" ! monad.leftIdentity[Live, Int, Int]
    "satisfy monad laws associativity" ! monad.associativity[Live, Int, Int, Int]
    "satisfy monad laws bindApConsistency" ! monad.bindApConsistency[Live, Int, Int]
  }

  implicit def LiveIntEqual: Equal[Live[Int]] = Equal.equal { (a, b) =>
    println(s"${a.value} <> ${b.value}")
    new Exception().printStackTrace(System.out)
    a.value == b.value
  }
}

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(classOf[LiveSpec]))
object LiveSpec extends LiveSpec

