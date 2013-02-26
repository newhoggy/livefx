package org.livefx

import org.livefx.script.Message

trait LiveValue[A] extends Publisher[Message[A]] {
  def value: A
}
