package org.livefx.volume

import org.livefx.debug._
import scalaz._
import Scalaz._

final class Branch[+A](val ts: List[Tree[A]]) extends Tree[A] {
  assert(ts.size < 5)
  println("------")
  
  final override def count = ts.size

  final override val size: Int = ts.foldRight(0)(_.size + _)

  final override def takeCount(count: Int): Branch[A] = Branch(ts.take(count))

  final override def dropCount(count: Int): Branch[A] = Branch(ts.drop(count))
  
  final override def toList[B >: A](acc: List[B]): List[B] = ts.foldRight(acc)((t, acc) => t.toList(acc))

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    val nts: List[Tree[B]] = ts.flatMap { t =>
      val result = if (i <= t.size) {
        (t.insert(i, value) match {
          case Branch(ca, cb, cc, cd) => List(Branch(ca, cb), Branch(cc, cd))
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

object Branch {
  def apply[A](ts: Tree[A]*) = new Branch(List(ts: _*))

  def apply[A](ts: List[Tree[A]]) = new Branch(ts)
  
  def unapplySeq[A](branch: Branch[A]) = List.unapplySeq(branch.ts)
}

