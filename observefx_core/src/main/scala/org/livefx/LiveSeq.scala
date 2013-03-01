package org.livefx

import org.livefx.script.Message

trait LiveSeq[A] extends Publisher[Message[A] with Undoable] {
  type Pub <: LiveSeq[A]
}
