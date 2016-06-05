package org.livefx.core.std

import org.livefx.core.disposal.Disposable

package object autoCloseable {
  implicit val disposableAutoCloseable_YYKh2cf = new Disposable[AutoCloseable] {
    protected override def onDispose(a: AutoCloseable): Unit = a.close()
  }
}
