package org.livefx

import org.livefx.script.Message
import org.livefx.script.Change

trait LiveSeq[A] extends Changeable[A, Change[A]] {
  type Pub <: LiveSeq[A]
}
