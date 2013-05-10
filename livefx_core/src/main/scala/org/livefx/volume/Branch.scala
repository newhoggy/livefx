package org.livefx.volume

import org.livefx.debug._
import scalaz._
import Scalaz._

trait Branch[+A] extends Tree[A] {
  override def insert[B >: A](index: Int, value: B): Branch[B]
  override def update[B >: A](index: Int, value: B): Branch[B]
  override def takeCount(count: Int): Branch[A]
  override def dropCount(count: Int): Branch[A]
}

final case class Branch0() extends Branch[Nothing] {
  final override val size: Int = 0
  final override def count: Int = 0
  final override def toList[B](acc: List[B]): List[B] = acc

  final override def takeCount(count: Int): Branch[Nothing] = if (count == 0) Branch0() else throw new IndexOutOfBoundsException
  final override def dropCount(count: Int): Branch[Nothing] = if (count == 0) Branch0() else throw new IndexOutOfBoundsException

  final override def insert[B](index: Int, value: B): Branch[B] = throw new IndexOutOfBoundsException

  final override def update[B](index: Int, value: B): Branch[B] = throw new IndexOutOfBoundsException

  final override def remove(index: Int): (Nothing, Tree[Nothing]) = throw new IndexOutOfBoundsException
}

final case class Branch1[+A](a: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size
  final override def count: Int = 1
  final override def toList[B >: A](acc: List[B]): List[B] = a.toList(acc)

  final override def takeCount(count: Int): Branch[A] = count match {
    case 0 => Branch0()
    case 1 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Branch[A] = count match {
    case 0 => this
    case 1 => Branch0()
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i <= a.size) {
      return a.insert(i, value) match {
        case BranchN(ca, cb, cc, cd) => Branch2(Branch2(ca, cb), Branch2(cc, cd))
        case Leaf(ca, cb, cc, cd) => Branch2(Leaf(ca, cb), Leaf(cc, cd))
        case newA => Branch1(newA)
      }
    }

    throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i < a.size) {
      return Branch1(a.update(i, value))
    }

    throw new IndexOutOfBoundsException
  }

  final override def remove(index: Int): (A, Tree[A]) = {
    val (v, na) = a.remove(index)

    if (na.count == 0) {
      (v, Branch0())
    } else {
      (v, Branch1(na))
    }
  }
}

final case class Branch2[+A](a: Tree[A], b: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size + b.size
  final override def count: Int = 2
  final override def toList[B >: A](acc: List[B]): List[B] = a.toList(b.toList(acc))

  final override def takeCount(count: Int): Branch[A] = count match {
    case 0 => Branch0()
    case 1 => Branch1(a)
    case 2 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Branch[A] = count match {
    case 0 => this
    case 1 => Branch1(b)
    case 2 => Branch0()
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i <= a.size) {
      return a.insert(i, value) match {
        case BranchN(ca, cb, cc, cd) => BranchN[B](Branch2(ca, cb), Branch2(cc, cd), b)
        case Leaf(ca, cb, cc, cd) => Branch[B](Leaf(ca, cb), Leaf(cc, cd), b)
        case na => Branch2(na, b)
      }
    }

    i -= a.size

    if (i <= b.size) {
      return b.insert(i, value) match {
        case BranchN(ca, cb, cc, cd) => BranchN[B](a, Branch2(ca, cb), Branch2(cc, cd))
        case Leaf(ca, cb, cc, cd) => Branch[B](a, Leaf(ca, cb), Leaf(cc, cd))
        case nb => Branch2(a, nb)
      }
    }

    throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i < a.size) {
      return Branch2(a.update(i, value), b)
    }

    i -= a.size

    if (i < b.size) {
      return Branch2(a, b.update(i, value))
    }

    throw new IndexOutOfBoundsException
  }

  final override def remove(index: Int): (A, Tree[A]) = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i < a.size) {
      val (v, na) = a.remove(i)
      return if (na.count == 0) (v, Branch1(b)) else (v, Branch2(na, b))
    }

    i -= a.size

    if (i < b.size) {
      val (v, nb) = b.remove(i)
      return if (nb.count == 0) (v, Branch1(a)) else (v, Branch2(a, nb))
    }

    throw new IndexOutOfBoundsException
  }
}

final class BranchN[+A](val ts: List[Tree[A]]) extends Branch[A] {
  final override def count = ts.size

  final override val size: Int = ts.foldRight(0)(_.size + _)

  final override def takeCount(count: Int): Branch[A] = Branch(ts.take(count), count)

  final override def dropCount(count: Int): Branch[A] = Branch(ts.drop(count), this.count - count)
  
  final override def toList[B >: A](acc: List[B]): List[B] = ts.foldRight(acc)((t, acc) => t.toList(acc))

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    val nts: List[Tree[B]] = ts.flatMap { t =>
      val result = if (i <= t.size) {
        (t.insert(i, value) match {
          case BranchN(ca, cb, cc, cd) => List(Branch2(ca, cb), Branch2(cc, cd))
          case Leaf(ca, cb, cc, cd) => List(Leaf(ca, cb), Leaf(cc, cd))
          case na: Tree[B] => List(na)
        }): List[Tree[B]]
      } else {
        Nil
      }
      
      i -= t.size
      result
    }
    
    if (i >= 0) {
      Branch(nts)
    } else {
      throw new IndexOutOfBoundsException
    }
  }

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    val nts = ts.map { t =>
      val result = if (i <= t.size) {
        t.update(i, value)
      } else {
        t
      }
      
      i -= t.size
      result
    }
    
    if (i >= 0) {
      Branch(nts)
    } else {
      throw new IndexOutOfBoundsException
    }
  }
  
  final override def remove(index: Int): (A, Tree[A]) = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    var v: Option[A] = None
    
    val nts = ts.flatMap { t =>
      val result = if (i <= t.size) {
        val (nv, nt) = t.remove(i)
        v = Some(nv)
        if (nt.count == 0) {
          List()
        } else {
          List(nt)
        }
      } else {
        List(t)
      }
      
      i -= t.size
      result
    }
    
    if (i >= 0) {
      v match {
        case Some(v) => (v, Branch(nts))
        case _ => throw new IndexOutOfBoundsException
      }
    } else {
      throw new IndexOutOfBoundsException
    }
  }
}

object BranchN {
  def apply[A](ts: Tree[A]*) = new BranchN(List(ts: _*))

  def apply[A](ts: List[Tree[A]]) = new BranchN(ts)
  
  def unapplySeq[A](branch: BranchN[A]) = List.unapplySeq(branch.ts)
}

final object Branch {
  def apply[A](ts: List[Tree[A]], count: Int): Branch[A] = ts match {
    case List()           => Branch0()
    case List(a)          => Branch1(a)
    case List(a, b)       => Branch2(a, b)
    case List(a, b, c)    => BranchN(a, b, c)
    case List(a, b, c, d) => BranchN(a, b, c, d)
    case ts => BranchN(ts)
  }

  def apply[A](ts: List[Tree[A]]): Branch[A] = ts match {
    case List()           => Branch0()
    case List(a)          => Branch1(a)
    case List(a, b)       => Branch2(a, b)
    case List(a, b, c)    => BranchN(a, b, c)
    case List(a, b, c, d) => BranchN(a, b, c, d)
    case ts => BranchN(ts)
  }
  
  def apply[A](ts: Tree[A]*): Branch[A] = ts.size match {
    case 0 => Branch0()
    case 1 => Branch1(ts(0))
    case 2 => Branch2(ts(0), ts(1))
    case 3 => BranchN(ts(0), ts(1), ts(2))
    case 4 => BranchN(ts(0), ts(1), ts(2), ts(3))
    case n => BranchN(ts.toList)
  }
}
