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

final case object Leaf0 extends Leaf[Nothing] {
  final override def size: Int = 0
  final override def count: Int = 0
  final override def takeCount(count: Int): this.type = if (count == 0) this else throw new IndexOutOfBoundsException
  final override def dropCount(count: Int): this.type = if (count == 0) this else throw new IndexOutOfBoundsException
  final override def toList[B](acc: List[B]): List[B] = acc
  
  final override def insert[B](index: Int, value: B): Leaf[B] = index match {
    case 0 => Leaf1(value)
    case _ => throw new IndexOutOfBoundsException
  }
  
  final override def update[B](index: Int, value: B): Leaf[B] = throw new IndexOutOfBoundsException

  final override def remove(index: Int): (Nothing, Tree[Nothing]) = throw new IndexOutOfBoundsException
}

final case class Leaf1[+A](a: A) extends Leaf[A] {
  final override def size: Int = 1
  final override def count: Int = 1
  final override def toList[B >: A](acc: List[B]): List[B] = a::acc

  final override def takeCount(count: Int): Leaf[A] = count match {
    case 0 => Leaf0
    case 1 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Leaf[A] = count match {
    case 0 => this
    case 1 => Leaf0
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Leaf[B] = index match {
    case 0 => Leaf2(value, a)
    case 1 => Leaf2(a, value)
    case _ => throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Leaf[B] = index match {
    case 0 => Leaf1(value)
    case _ => throw new IndexOutOfBoundsException
  }

  final override def remove(index: Int): (A, Tree[A]) = index match {
    case 0 => (a, Leaf0)
    case _ => throw new IndexOutOfBoundsException
  }
}

final case class Leaf2[+A](a: A, b: A) extends Leaf[A] {
  final override def size: Int = 2
  final override def count: Int = 2
  final override def toList[B >: A](acc: List[B]): List[B] = a::b::acc

  final override def takeCount(count: Int): Leaf[A] = count match {
    case 0 => Leaf0
    case 1 => Leaf1(a)
    case 2 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Leaf[A] = count match {
    case 0 => this
    case 1 => Leaf1(b)
    case 2 => Leaf0
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Leaf[B] = {
    index match {
      case 0 => Leaf3(value, a, b)
      case 1 => Leaf3(a, value, b)
      case 2 => Leaf3(a, b, value)
      case _ => throw new IndexOutOfBoundsException
    }
  }

  final override def update[B >: A](index: Int, value: B): Leaf[B] = index match {
    case 0 => Leaf2(value, b)
    case 1 => Leaf2(a, value)
    case _ => throw new IndexOutOfBoundsException
  }

  final override def remove(index: Int): (A, Tree[A]) = index match {
    case 0 => (a, Leaf1(b))
    case 1 => (b, Leaf1(a))
    case _ => throw new IndexOutOfBoundsException
  }
}

final case class Leaf3[+A](a: A, b: A, c: A) extends Leaf[A] {
  final override def size: Int = 3
  final override def count: Int = 3
  final override def toList[B >: A](acc: List[B]): List[B] = a::b::c::acc

  final override def takeCount(count: Int): Leaf[A] = count match {
    case 0 => Leaf0
    case 1 => Leaf1(a)
    case 2 => Leaf2(a, b)
    case 3 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Leaf[A] = count match {
    case 0 => this
    case 1 => Leaf2(b, c)
    case 3 => Leaf1(c)
    case 4 => Leaf0
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Leaf[B] = index match {
    case 0 => Leaf(value, a, b, c)
    case 1 => Leaf(a, value, b, c)
    case 2 => Leaf(a, b, value, c)
    case 3 => Leaf(a, b, c, value)
    case _ => throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Leaf[B] = index match {
    case 0 => Leaf3(value, b, c)
    case 1 => Leaf3(a, value, c)
    case 2 => Leaf3(a, b, value)
    case _ => throw new IndexOutOfBoundsException
  }

  final override def remove(index: Int): (A, Tree[A]) = index match {
    case 0 => (a, Leaf2(b, c))
    case 1 => (b, Leaf2(a, c))
    case 2 => (c, Leaf2(a, b))
    case _ => throw new IndexOutOfBoundsException
  }
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
  def apply[A](vs: List[A]): Leaf[A] = {
    vs match {
      case List()           => Leaf0
      case List(a)          => Leaf1(a)
      case List(a, b)       => Leaf2(a, b)
      case List(a, b, c)    => Leaf3(a, b, c)
      case List(a, b, c, d) => LeafN(a, b, c, d)
      case _ => LeafN(vs)
    }
  }
  
  def apply[A](vs: A*): Leaf[A] = {
    vs match {
      case List()           => Leaf0
      case List(a)          => vs match { case List(a)           => Leaf1(a)       }
      case List(a, b)       => vs match { case List(a, b)        => Leaf2(a, b)    }
      case List(a, b, c)    => vs match { case List(a, b, c)     => Leaf3(a, b, c) }
      case List(a, b, c, d) => vs match { case List(a, b, c, d)  => Leaf(vs: _*)   }
      case _ => LeafN(vs: _*)
    }
  }
  
  def unapplySeq[A](leaf: Leaf[A]) = leaf match {
    case Leaf0          => Some(Nil)
    case Leaf1(a)       => Some(List(a))
    case Leaf2(a, b)    => Some(List(a, b))
    case Leaf3(a, b, c) => Some(List(a, b, c))
    case LeafN(vs)      => Some(vs)
  }
}
