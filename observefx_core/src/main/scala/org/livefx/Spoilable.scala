package org.livefx

import org.livefx.script.Message
import org.livefx.script.Spoil

trait Spoilable[A] extends Publisher[Message[A]] {
  private val spoilListeners = new HashSet[Boolean => Unit]
  private var _spoiled: Boolean = true
  
  protected def spoil(): Unit = if (!_spoiled) {
    _spoiled = true
    publish(Spoil)
  }
  
  protected def unspoil(): Unit = _spoiled = false
  
  protected def notifySpoilListeners(): Unit = for (spoilListener <- spoilListeners) spoilListener(true)
}