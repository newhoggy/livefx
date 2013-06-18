package org.livefx.script

import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.HashSet
import org.livefx.Spoilable
import java.util.concurrent.atomic.AtomicReference

trait Message[+A]

case class Change[@specialized(Boolean, Int, Long, Double) +A](oldElem: A, newElem: A) extends Message[A]

sealed case class Spoil(
    val renewables: AtomicReference[HashSet[Spoilable]],
    val trace: List[StackTraceElement] = Nil) extends Message[Nothing]

case object Ping extends Message[Nothing]
