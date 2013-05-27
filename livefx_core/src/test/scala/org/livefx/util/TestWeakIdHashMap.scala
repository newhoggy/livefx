package org.livefx.util

import org.junit.Test
import org.junit.Assert

class TestWeakIdHashMap {
  @Test
  def testDuplicates(): Unit = {
    val map = new WeakIdHashMap[String, Int]
    var s1 = "1"
    map += (s1 -> 1)
    map += (s1 -> 1)
    
    Assert.assertEquals(map.toList, List(("1", 1)))
  }

  @Test
  def testTwoEntries(): Unit = {
    val map = new WeakIdHashMap[String, Int]
    var s1 = "1"
    var s2 = "2"
    map += (s1 -> 1)
    map += (s2 -> 2)
    
    Assert.assertEquals(map.toSet, Set(("1", 1), ("2", 2)))
  }

  @Test
  def testWeakness(): Unit = {
    val map = new WeakIdHashMap[String, Int]
    var s1 = new String("1")
    var s2 = new String("2")
    map += (s1 -> 1)
    map += (s2 -> 2)
    s2 = null
    System.gc()
    Assert.assertEquals(map.toSet, Set(("1", 1)))
  }

  @Test
  def testKeyIdentity(): Unit = {
    val map = new WeakIdHashMap[String, Int]
    var s1 = new String("1")
    var s2 = new String("1")
    map += (s1 -> 1)
    map += (s2 -> 2)
    Assert.assertEquals(map.toSet, Set(("1", 1), ("1", 2)))
  }
}
