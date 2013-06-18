package org.livefx

import scalaz._
import Scalaz._
import effect._
import scalaz.scalacheck.ScalazProperties._
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.Suite
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll

class LiveSpec extends Spec {
  def apLikeDerived[M[_], A, B](fa: M[A], f: M[A => B])(implicit M: Monad[M], FB: Equal[M[B]]): Boolean = {
    FB.equal(M.ap(fa)(f), M.bind(f)(f => M.map(fa)(f)))
  }

  def bindApConsistency[M[_], X, Y](implicit M: Monad[M], amx: Arbitrary[M[X]], af: Arbitrary[M[X => Y]], emy: Equal[M[Y]]) = {
    forAll(apLikeDerived[M, X, Y] _)
  }

  "Live" should {
    "satisfy monad laws rightIdentity" ! monad.rightIdentity[Live, Int]
    "satisfy monad laws leftIdentity" ! monad.leftIdentity[Live, Int, Int]
    "satisfy monad laws associativity" ! monad.associativity[Live, Int, Int, Int]
    "satisfy monad laws bindApConsistency" ! bindApConsistency[Live, Int, Int]
  }

  implicit def LiveIntEqual: Equal[Live[Int]] = Equal.equal { (a, b) => a.value == b.value }
}

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(classOf[LiveSpec]))
object LiveSpec extends LiveSpec

trait LiveSpecTrait
