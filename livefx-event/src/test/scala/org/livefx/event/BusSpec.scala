package org.livefx.event

import org.specs2.mutable.Specification

class BusSpec extends Specification {
  "Bus" should {
    "should implement map" ! {
      val es = Bus[Int]
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

//    "should implement flatMap" ! {
//      val bs = Bus[Boolean]
//      val ls = Bus[Int]
//      val rs = Bus[Int]
//      val zs = bs.flatMap(v => if (v) ls else rs)
//      var zsValues = List.empty[Int]
//      zs.subscribe(v => zsValues ::= v)
//
//      ls.publish(0)
//      rs.publish(1)
//      bs.publish(false)
//      ls.publish(2)
//      rs.publish(3)
//      bs.publish(true)
//      ls.publish(4)
//      rs.publish(5)
//      bs.publish(false)
//      ls.publish(6)
//      rs.publish(7)
//      bs.publish(true)
//      ls.publish(8)
//      rs.publish(9)
//      zsValues must_== List(8, 7, 4, 3)
//    }

    "should implement |" ! {
      val ltBus = Bus[Int]
      val rtBus = Bus[Int]
      val combinedBus = ltBus | rtBus
      var zsValues = List.empty[Int]
      combinedBus.subscribe(zsValues ::= _)
      ltBus.publish(0)
      rtBus.publish(1)
      ltBus.publish(2)
      rtBus.publish(3)
      zsValues must_== List(3, 2, 1, 0)
    }

//    "should implement impeded" ! {
//      val es = Bus[Int]
//      val esi = es.impeded
//      var esiValues = List.empty[Int]
//      esi.subscribe(v => esiValues ::= v)
//      esiValues must_== List()
//      es.publish(0)
//      esiValues must_== List()
//      es.publish(1)
//      esiValues must_== List(0)
//      es.publish(2)
//      esiValues must_== List(1, 0)
//    }
  }
}
