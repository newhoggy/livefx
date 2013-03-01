package org.livefx

import org.livefx.script.Message

trait LiveSeq[A] extends Changeable[Message[A] with Undoable] {
  type Pub <: LiveSeq[A]
}
