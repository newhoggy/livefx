package org.livefx

import scala.annotation.tailrec
import scala.reflect.ClassTag

sealed abstract class VirtualEntry[+T] {
  def size: Int
}

case class VirtualValue[T](value: T) extends VirtualEntry[T] {
  final override def size = 1
}

case class VirtualSpace(size: Int) extends VirtualEntry[Nothing] {
  assert(size > 0)
}

case class VirtualBuffer[T](aSide: List[VirtualEntry[T]], zSide: List[VirtualEntry[T]]) {
  def reverse = VirtualBuffer(zSide, aSide)
  
  @tailrec
  final def moveImpl(steps: Int): VirtualBuffer[T] = {
    assert(steps >= 0)

    if (steps > 0) {
      zSide match {
        case Nil => throw new IndexOutOfBoundsException
        case ze::zs =>
          if (steps > ze.size) {
            VirtualBuffer(ze::aSide, zs).moveImpl(steps - ze.size)
          } else {
            aSide match {
              case Nil => VirtualBuffer(List(VirtualSpace(steps)), VirtualSpace(ze.size - steps)::zs)
              case VirtualSpace(space)::as => VirtualBuffer(VirtualSpace(space + steps)::as, VirtualSpace(ze.size - steps)::zs)
              case vv => VirtualBuffer(VirtualSpace(steps)::aSide, VirtualSpace(ze.size - steps)::zs)
            }
          }
      }
    } else {
      this
    }
  }
  
  def move(steps:Int): VirtualBuffer[T] = {
    if (steps >= 0) {
      moveImpl(steps)
    } else {
      reverse.moveImpl(-steps).reverse
    }
  }
}

trait LiveSized {
  
}

trait SizeNode[A] {
  def size: Int
}

class SizeBranch[A] extends SizeNode[A] {
  override def size: Int = 1
}

class SizeLeaf[A: ClassTag](initialCapacity: Int = 32) extends SizeNode[A] {
  private val elems: Array[A] = new Array[A](initialCapacity)
  private val sizes: Array[Int] = new Array[Int](initialCapacity)
  private var aIndex: Int = 0
  private var zIndex: Int = initialCapacity
  override def size: Int = {
    var calculatedSize = 0

    for (i <- 0 to aIndex) {
      calculatedSize = calculatedSize + sizes(i)
    }
    
    for (i <- zIndex to initialCapacity) {
      calculatedSize = calculatedSize + sizes(i)
    }
    
    calculatedSize
  }
}
