package org.livefx

import org.livefx.script.Change

trait Changeable[A] extends Publisher {
  type Pub <: Changeable[A]
  
  lazy val changes = new EventSource[Pub, Change[A] with Undoable](publisher)
}
