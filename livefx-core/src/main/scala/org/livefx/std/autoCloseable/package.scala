package org.livefx.std

import org.livefx.disposal.Disposable

package object autoCloseable {
  implicit val disposableAutoCloseable_YYKh2cf = new Disposable[AutoCloseable] {
    protected override def onDispose(a: AutoCloseable): Unit = a.close()
  }
}
