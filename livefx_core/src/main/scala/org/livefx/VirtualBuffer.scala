package org.livefx

import scala.annotation.tailrec

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
