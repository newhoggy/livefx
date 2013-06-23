package org.livefx.dependency

import org.livefx.script.Spoil

object Independent extends Live[Int] {
  final override def spoilSink: EventSink[Spoil] = ???

  final override def spoils: Events[Spoil] = NoEvents
  
  final override def value: Int = 0
}
