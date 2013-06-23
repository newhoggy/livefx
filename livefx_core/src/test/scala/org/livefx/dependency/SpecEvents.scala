package org.livefx.dependency

import org.specs2.mutable.SpecificationWithJUnit

class SpecLive extends SpecificationWithJUnit {
  "dependency.Live" should {
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
