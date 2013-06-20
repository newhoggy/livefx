package org.livefx

import org.livefx.script.Spoil

object Independent extends Dependency {
  final override def spoilSink: org.livefx.EventSink[Spoil] = ???

  final override def spoils: Events[Spoil] = NoEvents
  
  final override def value: Int = 0
}
