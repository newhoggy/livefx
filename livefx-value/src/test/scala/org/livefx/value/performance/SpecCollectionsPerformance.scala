package org.livefx.value.performance

import org.specs2.mutable.Specification

class SpecCollectionsPerformance extends Specification {
  "list" >> {
    var x: List[Int] = Nil
    val a = System.nanoTime()
    for (i <- 1 to 1000000) {
      x = i::x
    }
    val b = System.nanoTime()
    println("Done: " + x.size)
    val c = System.nanoTime()
    println("Done: " + x.zipWithIndex.size)
    val d = System.nanoTime()

    ok
  }

  "array" >> {
    val x: Array[Int] = new Array[Int](1000000)
    val a = System.nanoTime()
    for (i <- 0 until 1000000) {
      x(i) = i
    }
    val b = System.nanoTime()
    println("Done: " + x.size)
    val c = System.nanoTime()
    println("Done: " + x.foldLeft(0)(_ + _))
    val d = System.nanoTime()

    ok
  }
}
