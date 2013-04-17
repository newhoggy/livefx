package org.livefx.gap

import org.livefx.Debug

case class Leaf[A](sizeL: Int, valuesL: List[A], valuesR: List[A], sizeR: Int) extends Tree[A] {
  type Self = Leaf[A]
  
  assert(sizeL == valuesL.size)
  assert(sizeR == valuesR.size)

  final override def insertL[B >: A](value: B)(implicit config: Config): Tree[B] = {
    assert(remainingCapacity > 0)
    this.copy(sizeL = sizeL + 1, valuesL = value::valuesL)
  }

  final override def insertR[B >: A](value: B)(implicit config: Config): Tree[B] = {
    this.copy(sizeR = sizeR + 1, valuesR = value::valuesR)
  }
  
  final def removeL(): Tree[A] = this.copy(sizeL = sizeL - 1, valuesL = valuesL.tail)

  final def removeR(): Tree[A] = this.copy(sizeR = sizeR - 1, valuesR = valuesR.tail)
  
  final def getL: A = valuesL.head
  
  final def getR: A = valuesR.head
  
  final def empty: Branch[A] = Branch[A](0, Nil, Leaf[A](), Nil, 0)
  
//  @tailrec
  final def moveBy(steps: Int): Leaf[A] = {
    if (steps > 0) {
      Debug.trace(s"moveBy[leaf-r]($steps): $this") {
        val result = Leaf(sizeL + 1, valuesR.head :: valuesL, valuesR.tail, sizeR - 1).moveBy(steps - 1)
        Debug.print(s"moveBy[leaf-result]: $result")
        result
      }
    } else if (steps < 0) {
      Debug.trace(s"moveBy[leaf-l]($steps): $this") {
        val result = Leaf(sizeL - 1, valuesL.tail, valuesL.head :: valuesR, sizeR + 1).moveBy(steps + 1)
        Debug.print(s"moveBy[leaf-result]: $result")
        result
      }
    } else {
      Debug.trace(s"moveBy[leaf-0]($steps): $this") {
        this
      }
    }
  }
  
  final override def moveTo(index: Int): Leaf[A] = {
    if (Debug.debug) println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }

  final def itemL: A = valuesL.head

  final def itemR: A = valuesR.head
  
  final override def remainingCapacity(implicit config: Config): Int = config.nodeCapacity - size
  
  final override def size: Int = sizeL + sizeR
  
  final def centre(implicit config: Config): Leaf[A] = this.moveTo(config.nodeCapacity / 2)
  
  final def dropL: Leaf[A] = Leaf(0, Nil, valuesR, sizeR)

  final def dropR: Leaf[A] = Leaf(sizeL, valuesL, Nil, 0)

  final override def divide(implicit config: Config): Either[(Tree[A], Tree[A]), (Tree[A], Tree[A])] = {
    val half = size / 2
    if (sizeL >= half) {
      valuesL.takeRight(1)
      Left((
          Leaf[A](
              half,
              valuesL.drop(sizeL - half),
              valuesL.take(sizeL - half).reverse,
              sizeL - half),
          Leaf[A](0, Nil, valuesR, sizeR)))
    } else {
      val result = Right((
          Leaf[A](sizeL, valuesL, Nil, 0),
          Leaf[A](
              sizeR - half,
              valuesR.take(sizeR - half).reverse,
              valuesR.drop(sizeR - half),
              half)))
      result
    }
  }

  def pretty(inFocus: Boolean): String = s"${(s"$sizeL)" :: valuesL.reverse.map(_.toString) ::: (if (inFocus) "*-*" else "*") :: valuesR.map(_.toString) ::: s"($sizeR" :: List()).mkString("[", ", ", "]")}"
}

object Leaf {
  def apply[A](valuesL: List[A], valuesR: List[A]): Leaf[A] = Leaf[A](valuesL.size, valuesL, valuesR, valuesR.size)
  def apply[A](): Leaf[A] = Leaf[A](0, Nil, Nil, 0)
}
