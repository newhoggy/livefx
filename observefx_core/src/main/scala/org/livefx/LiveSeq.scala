package org.livefx

import org.livefx.script.Message

trait LiveSeq[A] extends Changeable[A] {
  type Pub <: LiveSeq[A]
}
