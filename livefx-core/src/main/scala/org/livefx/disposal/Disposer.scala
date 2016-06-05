package org.livefx.disposal

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.livefx.std.autoCloseable._
import org.livefx.syntax.disposable._
import org.livefx.syntax.std.atomicReference._

class Disposer extends Closeable {
  private val disposables = new AtomicReference[Closeable](Closed)
  
  def disposes[D: Disposable](disposable: D): D = {
    disposables.update(_ ++ disposable.asCloseable)
    disposable
  }

  override def close(): Unit = disposables.getAndSet(Closed).dispose()
}
