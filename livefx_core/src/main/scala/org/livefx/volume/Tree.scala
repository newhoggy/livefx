package org.livefx.volume

import org.livefx.debug._

trait Tree[+A, M] {
  def size: Int
  def count: Int
  def volume: M
  def insert[B >: A <% M](index: Int, value: B): Tree[B, M]
  def update[B >: A <% M](index: Int, value: B): Tree[B, M]
  def remove(index: Int): (A, Tree[A, M])
  def takeCount(count: Int): Tree[A, M]
  def dropCount(count: Int): Tree[A, M]
  def toList[B >: A](acc: List[B] = Nil): List[B]
}
