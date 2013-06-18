package org.livefx.script

import scala.collection.mutable.ArrayBuffer

trait Message[+A]

case class Change[@specialized(Boolean, Int, Long, Double) +A](oldElem: A, newElem: A) extends Message[A]

trait Validity extends Message[Nothing] {
  def trace: List[StackTraceElement]
}

sealed case class Invalid(val trace: List[StackTraceElement] = Nil) extends Validity

sealed case class Valid(val trace: List[StackTraceElement] = Nil) extends Validity

case object Ping extends Message[Nothing]
