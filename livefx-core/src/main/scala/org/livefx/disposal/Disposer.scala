package org.livefx.disposal

import java.util.concurrent.atomic.AtomicReference

import org.livefx.syntax.std.atomicReference._

class Disposer extends Disposable {
  private val disposables = new AtomicReference[Disposable](Disposed)
  
  def disposes[D <: Disposable](disposable: D): D = {
    disposables.update(_ ++ disposable)
    disposable
  }

  def closes[D <: AutoCloseable](closeable: D): D = {
    disposables.update(_ ++ Disposable(closeable))
    closeable
  }

  override def onDispose(): Unit = disposables.getAndSet(Disposed).dispose()
}
