package org.livefx.volume

import org.livefx.debug._

trait Tree[+A, M] {
  def size: Int
  def count: Int
  def volume: Int
  def insert[B >: A](index: Int, value: B)(implicit hm: HasMonoid[B, Int]): Tree[B, M]
  def update[B >: A](index: Int, value: B)(implicit hm: HasMonoid[B, Int]): Tree[B, M]
  def remove(index: Int)(implicit hm: HasMonoid[A, Int]): (A, Tree[A, M])
  def takeCount(count: Int)(implicit hm: HasMonoid[A, Int]): Tree[A, M]
  def dropCount(count: Int)(implicit hm: HasMonoid[A, Int]): Tree[A, M]
  def toList[B >: A](acc: List[B] = Nil): List[B]
}
