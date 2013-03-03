package org.livefx.script

import scala.collection.mutable.ArrayBuffer

trait Message[+A]

trait Change[+A] extends Message[A]

case class Include[+A](location: Location, elem: A) extends Change[A] {
  def this(elem: A) = this(NoLo, elem)
}

case class Update[+A](location: Location, elem: A, oldElem: A) extends Change[A] {
  def this(elem: A, oldElem: A) = this(NoLo, elem, oldElem)
}

case class Remove[+A](location: Location, elem: A) extends Change[A] {
  def this(elem: A) = this(NoLo, elem)
}

case class Reset[+A]() extends Change[A]

class Script[A] extends ArrayBuffer[Change[A]] with Change[A] {
  override def toString(): String = {
    var res = "Script("
    var it = this.iterator
    var i = 1
    while (it.hasNext) {
      if (i > 1)
        res = res + ", "
      res = res + "[" + i + "] " + it.next
      i += 1
    }
    res + ")"
  }
}

sealed class Spoil extends Message[Nothing]

case object Spoil extends Spoil

case object Ping extends Message[Nothing]