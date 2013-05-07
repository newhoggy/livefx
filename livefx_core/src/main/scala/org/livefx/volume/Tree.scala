package org.livefx.volume

import org.livefx.debug._

trait Tree[+A] {
  def size: Int
  def count: Int
  def volume: Int
  def insert[B >: A](index: Int, value: B)(implicit hm: HasMonoid[B, Int]): Tree[B]
  def update[B >: A](index: Int, value: B)(implicit hm: HasMonoid[B, Int]): Tree[B]
  def remove(index: Int)(implicit hm: HasMonoid[A, Int]): (A, Tree[A])
  def takeCount(count: Int)(implicit hm: HasMonoid[A, Int]): Tree[A]
  def dropCount(count: Int)(implicit hm: HasMonoid[A, Int]): Tree[A]
  def toList[B >: A](acc: List[B] = Nil): List[B]
}
