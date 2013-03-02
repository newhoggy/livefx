package org.livefx

trait Events[+P, +E] {
  def subscribe(subscriber: (P, E) => Unit): Disposable
  def unsubscribe(subscriber: (P, E) => Unit): Unit
}
