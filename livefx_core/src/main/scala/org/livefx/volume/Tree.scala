package org.livefx.volume

import org.livefx.debug._
import scala.annotation.tailrec
import org.livefx.LeftOrRight

trait Tree[+A] {
  def size: Int
  def count: Int
  def insert[B >: A](index: Int, value: B): Tree[B]
  def update[B >: A](index: Int, value: B): Tree[B]
  def remove(index: Int): (A, Tree[A])
  def takeCount(count: Int): Tree[A]
  def dropCount(count: Int): Tree[A]
  def toList[B >: A](acc: List[B] = Nil): List[B]
}
