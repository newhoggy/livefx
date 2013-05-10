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
          case BranchN(ca, cb, cc, cd) => List(BranchN(ca, cb), BranchN(cc, cd))
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
    case List(a)          => BranchN(a)
    case List(a, b)       => BranchN(a, b)
    case List(a, b, c)    => BranchN(a, b, c)
    case List(a, b, c, d) => BranchN(a, b, c, d)
    case ts => BranchN(ts)
  }

  def apply[A](ts: List[Tree[A]]): Branch[A] = ts match {
    case List()           => Branch0()
    case List(a)          => BranchN(a)
    case List(a, b)       => BranchN(a, b)
    case List(a, b, c)    => BranchN(a, b, c)
    case List(a, b, c, d) => BranchN(a, b, c, d)
    case ts => BranchN(ts)
  }
  
  def apply[A](ts: Tree[A]*): Branch[A] = ts.size match {
    case 0 => Branch0()
    case 1 => BranchN(ts(0))
    case 2 => BranchN(ts(0), ts(1))
    case 3 => BranchN(ts(0), ts(1), ts(2))
    case 4 => BranchN(ts(0), ts(1), ts(2), ts(3))
    case n => BranchN(ts.toList)
  }
}
