package org.livefx

import org.livefx.trees.indexed.Tree
import org.livefx.script.Change

trait LiveTreeSeqBinding[A] extends LiveBinding[Tree[A]] with LiveTreeSeq[A] {
  type Pub = LiveTreeSeqBinding[A]
  
  def asLiveValue: LiveValue[Tree[A]] = this

  lazy val _changes = new EventSource[Pub, Change[A]](publisher)

  override def changes: Events[Pub, Change[A]] = _changes
}
