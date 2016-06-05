package org.livefx.disposal

object Disposed extends Disposable {
  final override def onDispose(): Unit = ()
}
