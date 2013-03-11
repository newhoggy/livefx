package org.livefx

import org.livefx.script.Change

trait Changeable[A, M <: Change[A]] extends Publisher {
  type Pub <: Changeable[A, M]
  
  protected lazy val changesSink = new EventSource[Pub, M](publisher)
  
  def changes: Events[Pub, M] = changesSink
}
