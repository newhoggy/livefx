package org.livefx

import org.livefx.script.Change

trait LiveSeqBinding[A] extends LiveSeq[A] {
  type Pub <: LiveSeqBinding[A]
  
  protected lazy val _changes = new EventSource[Pub, Change[A]](publisher)

  override def changes: Events[Pub, Change[A]] = _changes
}
