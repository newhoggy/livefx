package org.livefx.trees.n23

import org.junit.Test

class TestTree {
  @Test
  def testMemoization(): Unit = {
    var tree = Tree[Int]
    var tree1 = tree.map(_.toString)
    var tree2 = tree.map(_.toString)
  }
}
