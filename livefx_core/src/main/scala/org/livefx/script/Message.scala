package org.livefx.script

import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.HashSet
import org.livefx.Spoilable
import java.util.concurrent.atomic.AtomicReference
import scalaz.concurrent.Atomic

case class Change[@specialized(Boolean, Char, Byte, Short, Int, Long, Double) +A](oldElem: A, newElem: A)

sealed case class Spoil(
    val trace: List[StackTraceElement] = Nil)
