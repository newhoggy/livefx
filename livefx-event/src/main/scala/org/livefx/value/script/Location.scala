package org.livefx.value.script

sealed abstract class Location

case class Start(index: Int) extends Location {
  final def asEnd(hintSize: Int): End = End(hintSize - index - 1)
  final def add(offset: Int): Start = Start(index + offset)
}

case class End(index: Int) extends Location {
  final def asStart(hintSize: Int): Start = Start(hintSize - index - 1)
  final def add(offset: Int): End = End(index + offset)
}

case object NoLo extends Location
