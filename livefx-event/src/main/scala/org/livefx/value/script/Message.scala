package org.livefx.value.script

case class Change[@specialized(Boolean, Char, Byte, Short, Int, Long, Double) +A](oldElem: A, newElem: A)

sealed case class Spoil(
    trace: List[StackTraceElement] = Nil)
