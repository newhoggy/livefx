package org.livefx.gap

abstract class Tree[+A] {
  def insertL[B >: A](value: B)(implicit config: Config): Tree[B]

  def insertR[B >: A](value: B)(implicit config: Config): Tree[B]

  def moveBy(steps: Int): Tree[A]
  
  def moveTo(index: Int): Tree[A]
  
  def itemL: A
  
  def itemR: A
  
  def sizeL: Int

  def sizeR: Int

  def size: Int
  
  def empty: Tree[A]
  
  def remainingCapacity(implicit config: Config): Int
  
  def divide(implicit config: Config): Either[(Tree[A], Tree[A]), (Tree[A], Tree[A])]
  
  def pretty(inFocus: Boolean): String
}
