package org.livefx.volume

import org.livefx.debug._
import scalaz._
import Scalaz._

final class Leaf[+A](val vs: List[A]) extends Tree[A] {
  assert(vs.size < 5)
  
  final override def count: Int = vs.size
  
  final override def size: Int = vs.size

  final override def takeCount(count: Int): Leaf[A] = Leaf(vs.take(count))

  final override def dropCount(count: Int): Leaf[A] = Leaf(vs.drop(count))

  final override def toList[B >: A](acc: List[B]): List[B] = vs:::acc

  final override def insert[B >: A](index: Int, value: B): Tree[B] = Leaf(vs.take(index) ::: value :: vs.drop(index))

  final override def update[B >: A](index: Int, value: B): Leaf[B] = Leaf(vs.take(index) ::: value :: vs.drop(index + 1))

  final override def remove(index: Int): (A, Tree[A]) = {
    println(s"--> ${this.toList(Nil)}.remove($index)")
    val myInit = vs.take(index)
    val myTail = vs.drop(index)
    (myTail.head, Leaf(myInit ::: myTail.tail))
  }
  
  final override def split: List[Tree[A]] = {
    def build(list: List[A]): List[List[A]] = list match {
      case a::b::c::tail => List(a, b, c)::build(tail)
      case a::b::tail => List(a, b)::build(tail)
      case a::tail => List(a)::build(tail)
      case _ => Nil
    }
    build(vs).map(Leaf(_))
  }
  
  final override def toString(): String = "Leaf" + vs.mkString("(", ", ", ")")
  
  def debugString: String = vs.mkString("(", ", ", ")")
}

final object Leaf {
  private val emptyLeaf = new Leaf[Nothing](Nil)
  
  def apply() = emptyLeaf
  
  def apply[A](values: List[A]) = new Leaf[A](values)
  
  def apply[A](vs: A*): Leaf[A] = if (vs.size > 0) Leaf(vs: _*) else emptyLeaf
  
  def unapplySeq[A](leaf: Leaf[A]) = List.unapplySeq(leaf.vs)
}
