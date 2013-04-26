package org.livefx.gap

import org.livefx.debug._
import scala.annotation.tailrec
import org.livefx.LeftOrRight

trait Tree[+A] {
  def size: Int
  def count: Int
  def insert[B >: A](index: Int, value: B): Tree[B]
  def takeCount(count: Int): Tree[A]
  def dropCount(count: Int): Tree[A]
}
