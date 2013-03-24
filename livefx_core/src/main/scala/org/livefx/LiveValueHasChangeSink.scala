package org.livefx

import org.livefx.script.Change

trait LiveValueHasChangeSink[A] extends LiveValue[A] {
  protected lazy val changesSink = new EventSource[Pub, Change[A]](publisher)
  
  def changes: Events[Pub, Change[A]] = changesSink
}
