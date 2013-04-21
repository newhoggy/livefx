package org.livefx.gap

import org.livefx.Debug
import org.livefx.debug._
import org.livefx.LeftOrRight

case class Leaf[A](valuesL: Items[A], valuesR: Items[A]) extends Tree[A] {
  type Self = Leaf[A]

  final def removeL(): Tree[A] = this.copy(valuesL = valuesL.tail)

  final def removeR(): Tree[A] = this.copy(valuesR = valuesR.tail)

  final def getL: A = valuesL.head

  final def getR: A = valuesR.head

  final def empty: Branch[A] = Branch[A](TreesNil, Leaf[A](), TreesNil)

  final override def sizeL = valuesL.count

  final override def sizeR = valuesR.count

  final def itemL: A = valuesL.head

  final def itemR: A = valuesR.head

  final override def remainingCapacity(implicit config: Config): Int = config.nodeCapacity - size

  final override def size: Int = sizeL + sizeR

  final def dropL: Leaf[A] = Leaf(ItemsNil, valuesR)

  final def dropR: Leaf[A] = Leaf(valuesL, ItemsNil)

  final override def divide(implicit config: Config): Either[(Tree[A], Tree[A]), (Tree[A], Tree[A])] = {
    val half = size / 2
    if (sizeL >= half) {
      val pivot = sizeL - half
      Left((
          Leaf[A](valuesL.drop(pivot), valuesL.take(pivot).reverse),
          Leaf[A](ItemsNil, valuesR)))
    } else {
      val pivot = sizeR - half
      Right((
          Leaf[A](valuesL, ItemsNil), 
          Leaf[A](valuesR.take(pivot).reverse, valuesR.drop(pivot))))
    }.postcondition{case LeftOrRight((l, r)) => l.size + r.size == this.size}
  }

  def pretty(inFocus: Boolean): String = s"${(s"$sizeL)" :: valuesL.toList.reverse.map(_.toString) ::: (if (inFocus) "*-*" else "*") :: valuesR.toList.map(_.toString) ::: s"($sizeR" :: List()).mkString("[", ", ", "]")}"
}

object Leaf {
  def apply[A](valuesL: List[A], valuesR: List[A]): Leaf[A] = Leaf[A](valuesL.foldRight(ItemsNil: Items[A])((a, b) => a::b), valuesR.foldRight(ItemsNil: Items[A])((a, b) => a::b))
  def apply[A](): Leaf[A] = Leaf[A](ItemsNil, ItemsNil)
}
