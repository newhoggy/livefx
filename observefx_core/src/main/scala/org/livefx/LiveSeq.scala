package org.livefx

import org.livefx.script.Message

trait LiveSeq[A] {
  lazy val changes = new EventSource[LiveSeq[A], Message[A] with Undoable](this)
}
