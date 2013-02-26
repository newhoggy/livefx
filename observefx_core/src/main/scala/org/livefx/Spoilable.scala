package org.livefx

trait Spoilable {
  def onSpoil(f: Boolean => Unit): Unit
}
