package org.livefx

import org.livefx.volume.RedBlackTree
import org.junit.Test

class TestVolumeTree {
  @Test
  def test1(): Unit = {
    var tree: RedBlackTree.Tree[String] = RedBlackTree.Leaf
    println(tree)
    tree = RedBlackTree.insert(tree, 0, "0", true)
    println(tree)
    tree = RedBlackTree.insert(tree, 1, "1", true)
    println(tree)
  }
}
