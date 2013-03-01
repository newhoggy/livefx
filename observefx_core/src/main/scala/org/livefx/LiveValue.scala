package org.livefx

import org.livefx.script.Message

trait LiveValue[A] {
  lazy val changes = new EventSource[LiveValue[A], Message[A]](this)
  
  def value: A
}
