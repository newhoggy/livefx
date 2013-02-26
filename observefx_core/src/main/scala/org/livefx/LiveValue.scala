package org.livefx

import scala.collection.mutable.Publisher
import org.livefx.script.Message

trait LiveValue[A] extends Publisher[Message[A]] {
  def value: A
}
