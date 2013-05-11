package org.livefx.volume

import org.livefx.debug._
import scalaz._
import Scalaz._

final class Branch[+A](val ts: List[Tree[A]]) extends Tree[A] {
  assert(ts.size < 5)
  
  final override def count = ts.size

  final override val size: Int = ts.foldRight(0)(_.size + _)

  final override def takeCount(count: Int): Branch[A] = Branch(ts.take(count))

  final override def dropCount(count: Int): Branch[A] = Branch(ts.drop(count))
  
  final override def toList[B >: A](acc: List[B]): List[B] = ts.foldRight(acc)((t, acc) => t.toList(acc))

  final def focus(index: Int): (List[Tree[A]], Int, List[Tree[A]]) = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var l: List[Tree[A]] = Nil
    var r: List[Tree[A]] = ts
    var i: Int = 0
    
    while (r.size > 1 && i + r.head.size <= index) {
      val rh = r.head
      l = rh::l
      r = r.tail
      i += rh.size
    }
    
    (l, i, r)
  }

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    println(s"--> ${this.debugString}.insert($index, $value)")
    if (index < 0) throw new IndexOutOfBoundsException
    
    focus(index) match {
      case x@(l, i, r) => {
        println(s"--> x: $x")
        val nt = r.head.insert(index - i, value)
        println(s"--> nt.size: ${nt.debugString}")

        val result = if (nt.count > 3) {
          println("--> a")
          Branch(l.reverse ::: nt.split ::: r.tail)
        } else if (nt.count > 0) {
          println("--> b")
          Branch(l.reverse ::: nt :: r.tail)
        } else {
          println("--> c")
          Branch(l.reverse ::: r)
        }
        
        println(s"--> result: ${result.debugString}")
        result
      }
    }
  }

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    println(s"--> $this.update($index, $value)")
    if (index < 0) throw new IndexOutOfBoundsException
    
    def doUpdate(i: Int, list: List[Tree[B]]): List[Tree[B]] = {
      if (i < 0) {
        list
      } else {
        list match {
          case x::xs =>
            if (xs.size > 0) {
              if (i < x.size) {
                x.update(i, value) match {
                  case nx if nx.size > 3 => nx.split ::: xs
                  case nx => nx :: xs
                }
              } else {
                x :: doUpdate(i - x.size, xs)
              }
            } else {
              List(x.update(i, value))
            }
          case _ => throw new IndexOutOfBoundsException
        }
      }
    }
    
    Branch(doUpdate(index, ts))
  }
  
  final override def remove(index: Int): (A, Tree[A]) = {
    println(s"--> ${this.debugString}.remove($index)")
    if (index < 0) throw new IndexOutOfBoundsException
    
    focus(index) match {
      case x@(l, i, r) => {
        r.head.remove(index - i) match {
          case (removed, nt) =>
            println(s"--> nt.size: ${nt.debugString}")
    
            val result = if (nt.count > 3) {
              Branch(l.reverse ::: nt.split ::: r.tail)
            } else if (nt.count > 0) {
              Branch(l.reverse ::: nt :: r.tail)
            } else {
              Branch(l.reverse ::: r.tail)
            }
            
            (removed, result)
        }
      }
    }
  }
  
  
  final override def split: List[Tree[A]] = {
    def build(list: List[Tree[A]]): List[List[Tree[A]]] = list match {
      case a::b::c::tail => List(a, b, c)::build(tail)
      case a::b::tail => List(a, b)::build(tail)
      case a::tail => List(a)::build(tail)
      case _ => Nil
    }
    build(ts).map(Branch(_))
  }
  
  final override def toString(): String = "Branch" + ts.mkString("(", ", ", ")")

  final override def debugString: String = ts.map(_.debugString).mkString("[", ", ", "]")
}

object Branch {
  def apply[A](ts: Tree[A]*) = new Branch(List(ts: _*))

  def apply[A](ts: List[Tree[A]]) = new Branch(ts)
  
  def unapplySeq[A](branch: Branch[A]) = List.unapplySeq(branch.ts)
}

