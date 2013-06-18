package org.livefx.script

import scala.collection.mutable.ArrayBuffer

trait Message[+A]

case class Change[@specialized(Boolean, Int, Long, Double) +A](oldElem: A, newElem: A) extends Message[A]

sealed case class Spoil(val renewable: Boolean, val trace: List[StackTraceElement] = Nil) extends Message[Nothing]

case object Ping extends Message[Nothing]
