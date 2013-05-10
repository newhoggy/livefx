package org.livefx.volume

import org.livefx.debug._
import scalaz._
import Scalaz._

final class Leaf[+A](val vs: List[A]) extends Tree[A] {
  final override def count: Int = vs.size
  
  final override def size: Int = vs.size

  final override def takeCount(count: Int): Leaf[A] = new Leaf(vs.take(count))

  final override def dropCount(count: Int): Leaf[A] = new Leaf(vs.drop(count))

  final override def toList[B >: A](acc: List[B]): List[B] = vs:::acc

  final override def insert[B >: A](index: Int, value: B): Leaf[B] = new Leaf(vs.take(index) ::: value :: vs.drop(index))

  final override def update[B >: A](index: Int, value: B): Leaf[B] = new Leaf(vs.take(index) ::: value :: vs.drop(index + 1))

  final override def remove(index: Int): (A, Tree[A]) = {
    val myInit = vs.take(index)
    val myTail = vs.drop(index)
    (myTail.head, Leaf(myInit ::: myTail.tail))
  }
}

final object Leaf {
  private val emptyLeaf = new Leaf[Nothing](Nil)
  
  def apply() = emptyLeaf
  
  def apply[A](values: List[A]) = new Leaf[A](values)
  
  def apply[A](vs: A*): Leaf[A] = if (vs.size > 0) Leaf(vs: _*) else emptyLeaf
  
  def unapplySeq[A](leaf: Leaf[A]) = List.unapplySeq(leaf.vs)
}
