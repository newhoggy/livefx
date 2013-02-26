package org.livefx

import org.livefx.script.Message

trait LiveValue[A] extends Publisher[Message[A]] with Spoilable {
  def value: A
}
