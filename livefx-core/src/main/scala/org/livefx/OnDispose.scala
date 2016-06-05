package org.livefx

import org.livefx.disposal.Disposable

object OnDispose {
  def apply(f: => Unit): Disposable = {
    new Disposable {
      override protected def onDispose(): Unit = f
    }
  }
}
