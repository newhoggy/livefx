package org.livefx.event

object Bus {
  def apply[A]: Bus[A] = SinkSource[A, A](identity)
}
