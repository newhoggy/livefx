package org.livefx

import org.livefx.trees.indexed.Tree
import org.livefx.script.Change

trait LiveSeqBinding[A] extends LiveBinding[Tree[A]] with LiveTreeSeq[A] {
  type Pub = LiveSeqBinding[A]
  
  def asLiveValue: LiveValue[Tree[A]] = this

  lazy val _changes = new EventSource[Pub, Change[A]](publisher)

  override def changes: Events[Pub, Change[A]] = _changes
}
