package org.livefx.trees.indexed

case class TreeSeq[+A](tree: Tree[A]) extends Seq[A] with Serializable {
  def iterator: Iterator[A] = tree.toList.iterator
  
  def apply(idx: Int): A = tree.lookup(idx).value
  
  def length: Int = tree.size
}
