package org.livefx

import org.livefx.script.Change

trait Changeable[A] extends Publisher {
  type Pub <: Changeable[A]
  
  protected lazy val changesSink = new EventSource[Pub, Change[A] with Undoable](publisher)
  
  def changes: Events[Pub, Change[A] with Undoable] = changesSink
}
