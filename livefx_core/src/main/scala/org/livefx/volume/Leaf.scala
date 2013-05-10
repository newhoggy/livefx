package org.livefx.volume

import org.livefx.debug._
import scalaz._
import Scalaz._

trait Leaf[+A] extends Tree[A] {
  override def insert[B >: A](index: Int, value: B): Leaf[B]
  override def update[B >: A](index: Int, value: B): Leaf[B]
  override def takeCount(count: Int): Leaf[A]
  override def dropCount(count: Int): Leaf[A]
}

final case class LeafN[+A](vs: List[A]) extends Leaf[A] {
  final override def count: Int = vs.size
  
  final override def size: Int = vs.size

  final override def takeCount(count: Int): Leaf[A] = Leaf(vs.take(count))

  final override def dropCount(count: Int): Leaf[A] = Leaf(vs.drop(count))

  final override def toList[B >: A](acc: List[B]): List[B] = vs:::acc

  final override def insert[B >: A](index: Int, value: B): Leaf[B] = LeafN(vs.take(index) ::: value :: vs.drop(index))

  final override def update[B >: A](index: Int, value: B): Leaf[B] = LeafN(vs.take(index) ::: value :: vs.drop(index + 1))

  final override def remove(index: Int): (A, Tree[A]) = {
    val myInit = vs.take(index)
    val myTail = vs.drop(index)
    (myTail.head, LeafN(myInit ::: myTail.tail))
  }
}

final object LeafN {
  def apply[A](vs: A*): LeafN[A] = LeafN(vs.toList)
  
  def unapplySeq[A](leaf: LeafN[A]) = List.unapplySeq(leaf.vs)
}

final object Leaf {
  private val emptyLeaf = LeafN()
  
  def apply() =  emptyLeaf
  
  def apply[A](vs: List[A]): Leaf[A] = {
    vs match {
      case List()           => emptyLeaf
      case List(a)          => Leaf(a)
      case List(a, b)       => Leaf(a, b)
      case List(a, b, c)    => Leaf(a, b, c)
      case List(a, b, c, d) => LeafN(a, b, c, d)
      case _ => LeafN(vs)
    }
  }
  
  def apply[A](vs: A*): Leaf[A] = if (vs.size > 0) LeafN(vs: _*) else emptyLeaf
  
  def unapplySeq[A](leaf: Leaf[A]) = leaf match {
    case LeafN(vs)      => Some(vs)
  }
}
