package org.livefx

import scala.reflect.internal.util.WeakHashSet

trait SimpleSpoilable extends Spoilable {
  private val spoilListeners = new HashSet[Boolean => Unit]
  private var _spoiled: Boolean = true
  
  protected def spoil(): Unit = if (!_spoiled) {
    _spoiled = true
    notifySpoilListeners()
  }
  
  protected def unspoil(): Unit = _spoiled = false
  
  protected def notifySpoilListeners(): Unit = for (spoilListener <- spoilListeners) spoilListener(true)

  def onSpoil(spoilListener: Boolean => Unit): Disposable = new Disposable {
    def dispose(): Unit = spoilListeners -= spoilListener
  }
}
