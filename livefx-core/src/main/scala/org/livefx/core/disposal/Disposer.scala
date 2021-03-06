package org.livefx.core.disposal

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.livefx.core.std.autoCloseable._
import org.livefx.core.syntax.disposable._
import org.livefx.core.syntax.std.atomicReference._

class Disposer extends Closeable {
  private val disposables = new AtomicReference[Closeable](Closed)

  final def +=[D: Disposable](disposable: D): D = disposes(disposable)

  final def disposes[D: Disposable](disposable: D): D = {
    disposables.update(_ ++ disposable.asCloseable)
    disposable
  }

  final override def close(): Unit = disposables.getAndSet(Closed).dispose()
}
