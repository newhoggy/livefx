package org.livefx

object OnDispose {
  def apply(f: => Unit): Disposable = {
    new Disposable {
      override protected def onDispose(): Unit = f
    }
  }
}
