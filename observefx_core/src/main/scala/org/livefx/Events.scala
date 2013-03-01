package org.livefx

trait Events[+P, +E] {
  def subscribe(subscriber: (P, E) => Unit): Unit
  def unsubscribe(subscriber: (P, E) => Unit): Unit
}
