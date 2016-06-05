package org.livefx

object Disposed extends Disposable {
  final override def onDispose(): Unit = ()
}
