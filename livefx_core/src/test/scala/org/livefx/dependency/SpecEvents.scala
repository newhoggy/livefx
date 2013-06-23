package org.livefx.dependency

import org.specs2.mutable.SpecificationWithJUnit

class SpecEvents extends SpecificationWithJUnit {
  "dependency.Events" should {
    "should implement map" ! {
      val es = new EventSource[Int]
      val fs = es.map(_ + 1)
      var esValues = List.empty[Int]
      var fsValues = List.empty[Int]
      es.subscribe(v => esValues ::= v)
      fs.subscribe(v => fsValues ::= v)
      es.publish(10)
      esValues must_== List(10)
      fsValues must_== List(11)
      es.publish(20)
      esValues must_== List(20, 10)
      fsValues must_== List(21, 11)
    }
    "should implement flatMap" ! {
      val bs = new EventSource[Boolean]
      val ls = new EventSource[Int]
      val rs = new EventSource[Int]
      val zs = bs.flatMap(v => if (v) ls else rs)
      var zsValues = List.empty[Int]
      zs.subscribe(v => zsValues ::= v)
      
      ls.publish(0)
      rs.publish(1)
      bs.publish(false)
      ls.publish(2)
      rs.publish(3)
      bs.publish(true)
      ls.publish(4)
      rs.publish(5)
      bs.publish(false)
      ls.publish(6)
      rs.publish(7)
      bs.publish(true)
      ls.publish(8)
      rs.publish(9)
      zsValues must_== List(8, 7, 4, 3)
    }
    "should implement |" ! {
      val es = new EventSource[Int]
      val fs = new EventSource[Int]
      val zs = es | fs
      var zsValues = List.empty[Int]
      zs.subscribe(v => zsValues ::= v)
      es.publish(0)
      fs.publish(1)
      es.publish(2)
      fs.publish(3)
      zsValues must_== List(3, 2, 1, 0)
    }
  }
}
