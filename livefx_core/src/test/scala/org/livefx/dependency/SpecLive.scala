package org.livefx.dependency

import org.specs2.mutable.SpecificationWithJUnit

class SpecLive extends SpecificationWithJUnit {
  "dependency.Live" should {
    "should implement map" ! {
      val liveA = Var[Int](0)
      val liveB = liveA.map(_ + 1)
      var as = List.empty[Int]
      var bs = List.empty[Int]
      liveA.value
      liveB.value
      val ref1 = liveA.spoils.subscribe(e => as ::= liveA.value)
      val ref2 = liveB.spoils.subscribe{e => bs ::= liveB.value}
      liveA.value = 1
      as must_== List(1)
      bs must_== List(2)
      liveA.value = 2
      as must_== List(2, 1)
      bs must_== List(3, 2)
    }
    "should implement flatMap" ! {
      val liveA = Var[Int](0)
      val liveB = Var[Int](0)
      val liveC = Var[Int](0)
      val liveD: Live[Int] = liveA.flatMap(value => if (value % 2 == 0) liveB else liveC)
      var ds = List.empty[Int]
      val subD = liveD.spoils.subscribe(_ => ds ::= 1)
      
      ds must_== List(8, 7, 4, 3)
    }
  }
}
