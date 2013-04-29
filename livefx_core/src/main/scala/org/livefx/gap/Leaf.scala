package org.livefx.gap

import org.livefx.Debug
import org.livefx.debug._
import org.livefx.LeftOrRight

trait Leaf[+A] extends Tree[A] {
  override def insert[B >: A](index: Int, value: B): Leaf[B]
  override def takeCount(count: Int): Leaf[A]
  override def dropCount(count: Int): Leaf[A]
}

final case object Leaf0 extends Leaf[Nothing] {
  final override def size: Int = 0
  final override def count: Int = 0
  final override def takeCount(count: Int): this.type = if (count == 0) this else throw new IndexOutOfBoundsException
  final override def dropCount(count: Int): this.type = if (count == 0) this else throw new IndexOutOfBoundsException
  final override def toList[B](acc: List[B]): List[B] = acc
  
  final override def insert[A](index: Int, value: A): Leaf[A] = index match {
    case 0 => Leaf1(value)
    case _ => throw new IndexOutOfBoundsException
  }
  
  final override def update[A](index: Int, value: A): Leaf[A] = throw new IndexOutOfBoundsException
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

  final override def update[B >: A](index: Int, value: B): Leaf[B] = {
    println(s"$this.update($index, $value)")
    index match {
      case 0 => Leaf2(value, b)
      case 1 => Leaf2(a, value)
      case _ => throw new IndexOutOfBoundsException
    }
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
    case 0 => Leaf4(value, a, b, c)
    case 1 => Leaf4(a, value, b, c)
    case 2 => Leaf4(a, b, value, c)
    case 3 => Leaf4(a, b, c, value)
    case _ => throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Leaf[B] = index match {
    case 0 => Leaf3(value, b, c)
    case 1 => Leaf3(a, value, c)
    case 2 => Leaf3(a, b, value)
    case _ => throw new IndexOutOfBoundsException
  }
}

final case class Leaf4[+A](a: A, b: A, c: A, d: A) extends Leaf[A] {
  final override def size: Int = 4
  final override def count: Int = 4
  final override def toList[B >: A](acc: List[B]): List[B] = a::b::c::d::acc

  final override def takeCount(count: Int): Leaf[A] = count match {
    case 0 => Leaf0
    case 1 => Leaf1(a)
    case 2 => Leaf2(a, b)
    case 3 => Leaf3(a, b, c)
    case 4 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Leaf[A] = count match {
    case 0 => this
    case 1 => Leaf3(b, c, d)
    case 2 => Leaf2(c, d)
    case 3 => Leaf1(d)
    case 4 => Leaf0
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Leaf[B] = index match {
    case 0 => LeafN(List(value, a, b, c, d), 5)
    case 1 => LeafN(List(a, value, b, c, d), 5)
    case 2 => LeafN(List(a, b, value, c, d), 5)
    case 3 => LeafN(List(a, b, c, value, d), 5)
    case 4 => LeafN(List(a, b, c, d, value), 5)
    case _ => throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Leaf[B] = index match {
    case 0 => Leaf4(value, b, c, d)
    case 1 => Leaf4(a, value, c, d)
    case 2 => Leaf4(a, b, value, d)
    case 3 => Leaf4(a, b, c, value)
    case _ => throw new IndexOutOfBoundsException
  }
}

final case class LeafN[+A](vs: List[A], size: Int) extends Leaf[A] {
  assert(vs.size == size)

  final override def count: Int = size

  final override def takeCount(count: Int): Leaf[A] = Leaf(vs.take(count), count)

  final override def dropCount(count: Int): Leaf[A] = Leaf(vs.drop(count), this.count - count)

  final override def toList[B >: A](acc: List[B]): List[B] = vs:::acc

  final override def insert[B >: A](index: Int, value: B): Leaf[B] = {
    LeafN(vs.take(index) ::: value :: vs.drop(index), size + 1)
  }

  final override def update[B >: A](index: Int, value: B): Leaf[B] = {
    LeafN(vs.take(index) ::: value :: vs.drop(index + 1), size)
  }
}

final object LeafN {
  def apply[A](vs: List[A]): LeafN[A] = LeafN(vs, vs.size)
}

final object Leaf {
  def apply[A](vs: List[A], count: Int): Leaf[A] = {
    assert(count == vs.size)
    count match {
      case 0 => Leaf0
      case 1 => vs match { case List(a)           => Leaf1(a)           }
      case 2 => vs match { case List(a, b)        => Leaf2(a, b)        }
      case 3 => vs match { case List(a, b, c)     => Leaf3(a, b, c)     }
      case 4 => vs match { case List(a, b, c, d)  => Leaf4(a, b, c, d)  }
      case count => LeafN(vs, count)
    }
  }
}
