package org.livefx

trait Subscriber[-Evt, -Pub] {
  def notify(pub: Pub, event: Evt): Unit
}
