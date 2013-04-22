package org.livefx.gap

import scala.annotation.tailrec

abstract class Items[+A] {
  def head: A
  def tail: Items[A]
  def count: Int
  @inline final def ::[B >: A](value: B): Items[B] = ItemsCons[B](value, this)
  @inline final def reverse: Items[A] = Items.reverse(this, ItemsNil)
  @inline final def drop(n: Int): Items[A] = Items.drop(this, n)
  @inline final def take(n: Int): Items[A] = Items.takeReverse(this, n).reverse
  @inline final def toList: List[A] = Items.toList(this)
  @inline final def prependReversed[B >: A](values: Items[B]): Items[B] = Items.takeAllReverse(this, values) 
}

final case class ItemsCons[A](head: A, tail: Items[A]) extends Items[A] {
  final override val count: Int = tail.count + 1
}

final case object ItemsNil extends Items[Nothing] {
  final override def head: Nothing = throw new IndexOutOfBoundsException
  final override def tail: ItemsCons[Nothing] = throw new IndexOutOfBoundsException
  final override def count: Int = 0
}

object Items {
  @tailrec
  def reverse[A](self: Items[A], result: Items[A]): Items[A] = self match {
    case ItemsCons(head, tail) => reverse(tail, head::result)
    case ItemsNil => result
  }

  @tailrec
  def drop[A](self: Items[A], n: Int): Items[A] = n match {
    case x if x > 0 => drop(self.tail, n - 1)
    case x if x == 0 => self
    case _ => throw new IndexOutOfBoundsException
  }

  @tailrec
  def takeReverse[A](self: Items[A], n: Int, result: Items[A] = ItemsNil): Items[A] = n match {
    case x if x > 0 => takeReverse(self.tail, n - 1, self.head:: result)
    case x if x == 0 => result
    case _ => throw new IndexOutOfBoundsException
  }

  @tailrec
  def takeAllReverse[A, B >: A](self: Items[A], result: Items[B] = ItemsNil): Items[B] = self match {
    case ItemsCons(head, tail) => takeAllReverse(tail, head::result)
    case ItemsNil => result
  }

  @tailrec
  def toList[A](self: Items[A], result: Items[A] = ItemsNil): List[A] = self match {
    case ItemsCons(head, tail) => toList(self.tail, self.head::result)
    case ItemsNil => Nil
  }
}
