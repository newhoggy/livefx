package org.livefx

import org.livefx.script.Change

trait Changeable[A] extends Publisher[Change[A] with Undoable] {
  type Pub <: Changeable[A]
  
  lazy val changes = new EventSource[Pub, A](publisher)
}
