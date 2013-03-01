package org.livefx

import org.livefx.script.Message

trait LiveSeq[A] extends Publisher[A] {
  type Pub <: LiveSeq[A]
}
