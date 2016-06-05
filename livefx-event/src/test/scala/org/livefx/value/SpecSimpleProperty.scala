package org.livefx

import org.livefx.value.script.Change
import org.specs2.mutable._

class SpecSimpleProperty extends Specification {
//  "get and set" >> {
//    var spoilCount = 0
//    val liveValue = Var[Int](0)
//
//    0     ==== liveValue.value
//    false ==== liveValue.spoiled
//    liveValue.value = 1
//    1     ==== liveValue.value
//    false ==== liveValue.spoiled
//
//    val subscription = liveValue.spoils.subscribe(_ => spoilCount += 1)
//    0     ==== spoilCount
//    liveValue.value = 2
//    1     ==== spoilCount
//    2     ==== liveValue.value
//    false ==== liveValue.spoiled
//
//    subscription.dispose()
//
//    liveValue.value = 3
//    1     ==== spoilCount
//    3     ==== liveValue.value
//    false ==== liveValue.spoiled
//    subscription.dispose()
//
//    ok
//  }

//  "change subscrier" >> {
//    var changes = List[Change[Int]]()
//    val liveValue = Var[Int](0)
//
//    val subscription = liveValue.changes.subscribe(change => changes = change::changes)
//    liveValue.value ==== 0
//    changes         ==== Nil
//
//    liveValue.value = 11
//    liveValue.value ==== 11
//    changes         ==== List(Change(0, 11))
//
//    liveValue.value = 12
//    liveValue.value ==== 12
//    changes         ==== List(Change(11, 12), Change(0, 11))
//
//    subscription.dispose()
//    liveValue.value = 13
//    liveValue.value ==== 13
//    changes         ==== List(Change(11, 12), Change(0, 11))
//  }

//  "weak change subscriber" >> {
//    var changes = List[Change[Int]]()
//    val liveValue = Var[Int](0)
//
//    var subscription = liveValue.changes.subscribe(change => changes = change::changes)
//    liveValue.value ==== 0
//    changes         ==== Nil
//
//    liveValue.value = 11
//    liveValue.value ==== 11
//    changes         ==== List(Change(0, 11))
//
//    liveValue.value = 12
//    liveValue.value ==== 12
//    changes         ==== List(Change(11, 12), Change(0, 11))
//
//    subscription = null
//    System.gc()
//    liveValue.value = 13
//    liveValue.value ==== 13
//    changes         ==== List(Change(11, 12), Change(0, 11))
//  }
//
//  "map" >> {
//    val liveValue = Var[Int](0)
//    val liveBinding = liveValue.map(_ * 2)
//    liveValue.value = 1
//    liveBinding.value ==== 2
//    liveValue.value = 10
//    liveBinding.value ==== 20
//  }
//
//  "for comprehension short" >> {
//    val liveA = Var[Short](0)
//    val liveB = Var[Short](0)
//    val liveC = Var[Short](0)
//    val liveZ = for {
//      a <- liveA
//      b <- liveB
//      c <- liveC
//    } yield a + b + c
//
//    liveZ.value ==== 0
//    liveB.value = 7
//    liveZ.value ==== 7
//    liveA.value = 3
//    liveZ.value ==== 10
//    liveC.value = 10
//    liveZ.value ==== 20
//    liveA.value = 1
//    liveZ.value ==== 18
//  }
//
//  "comparisons" >> {
//    import LiveNumeric.Implicits._
//
//    val liveA = Var[Int](0)
//    val liveB = Var[Int](0)
//    val liveC = Var[Int](0)
//    val liveZ = liveA + liveB + liveC
//
//    liveZ.value ==== 0
//    liveB.value = 7
//    liveZ.value ==== 7
//    liveA.value = 3
//    liveZ.value ==== 10
//    liveC.value = 10
//    liveZ.value ==== 20
//    liveA.value = 1
//    liveZ.value ==== 18
//  }
//
//  "spoil count" >> {
//    import LiveNumeric.Implicits._
//    val liveA = Var[Int](0)
//    val liveB = Var[Int](0)
//    val liveC = liveA + liveB
//    val liveACount = liveA.spoilCount
//    val liveBCount = liveB.spoilCount
//    val liveCCount = liveC.spoilCount
//    val live3Count = for (a <- liveA; b <- liveB; c <- liveC) yield (a, b, c)
//
//    live3Count.value ==== (0, 0, 0)
//    liveA.value = 1
//    live3Count.value ==== (1, 0, 1)
//    liveB.value = 1
//    live3Count.value ==== (1, 1, 2)
//    liveA.value = 2
//    live3Count.value ==== (2, 1, 3)
//    liveB.value = 2
//    live3Count.value ==== (2, 2, 4)
//    liveA.value = 3
//    live3Count.value ==== (3, 2, 5)
//  }
//
//  "boolean" >> {
//    val liveA = Var[Boolean](false)
//    val liveB = Var[Boolean](false)
//    val liveAnd = liveA && liveB
//    val liveOr = liveA || liveB
//    val liveNot = !liveA
//    val live3 = for (and <- liveAnd; or <- liveOr; not <- liveNot) yield (and, or, not)
//
//    live3.value ==== (false, false, true)
//    liveA.value = true
//    live3.value ==== (false, true, false)
//    liveB.value = true
//    live3.value ==== (true, true, false)
//    liveA.value = false
//    live3.value ==== (false, true, true)
//    liveB.value = false
//    live3.value ==== (false, false, true)
//  }
//
//  "glitch" >> {
//    import LiveNumeric.Implicits._
//    val liveA = Var[Int](0)
//    val liveB1 = liveA.map(x => x)
//    val liveB2 = liveA.map(x => x)
//    val liveC = liveB1 + liveB2
//    var changes = List.empty[Change[Int]]
//
//    val subscription = liveC.changes.subscribe { change => changes ::= change }
//    liveA.value = 1
//
//    ok
//  }
}
