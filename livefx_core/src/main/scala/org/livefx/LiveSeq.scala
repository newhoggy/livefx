package org.livefx

import org.livefx.util.Memoize
import org.livefx.trees.indexed.Tree
import org.livefx.trees.indexed.Leaf
import org.livefx.script._

import scalaz.Monoid

trait LiveSeq[+A] extends Spoilable {
  type Pub <: LiveSeq[A]
  
  def changes: Events[Pub, Change[A]]
  
  def size: LiveValue[Int]
  
  def asLiveValue: LiveValue[Tree[A]]
  
  def asLiveSeq: LiveSeq[A] = this

  def fold[B >: A](implicit monoid: Monoid[B]): LiveValue[B]
  
  def map[B](f: A => B): LiveSeq[B]
}
