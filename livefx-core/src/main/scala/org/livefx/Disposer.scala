package org.livefx

import java.util.concurrent.atomic.AtomicReference

import org.livefx.syntax.std.atomicReference._

class Disposer extends Disposable {
  private val disposables = new AtomicReference[Disposable](Disposed)
  
  def disposes[D <: Disposable](disposable: D): D = {
    disposables.update(disposable ++ _)

    disposable
  }

  override def onDispose(): Unit = disposables.getAndSet(Disposed).dispose()
}
