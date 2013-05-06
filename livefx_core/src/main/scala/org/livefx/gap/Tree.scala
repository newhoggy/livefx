package org.livefx.gap

import org.livefx.debug._
import scala.annotation.tailrec
import org.livefx.LeftOrRight

trait Tree[+A] {
  def size: Int
  def count: Int
  def volume: Int
  def insert[B >: A](index: Int, value: B)(implicit vg: B => Int): Tree[B]
  def update[B >: A](index: Int, value: B)(implicit vg: B => Int): Tree[B]
  def remove(index: Int)(implicit vg: A => Int): (A, Tree[A])
  def takeCount(count: Int)(implicit vg: A => Int): Tree[A]
  def dropCount(count: Int)(implicit vg: A => Int): Tree[A]
  def toList[B >: A](acc: List[B] = Nil): List[B]
}
