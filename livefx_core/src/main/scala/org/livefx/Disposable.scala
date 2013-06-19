package org.livefx

import scala.concurrent._
import java.util.concurrent.atomic.AtomicBoolean

trait Disposable {
  private var _disposed = new AtomicBoolean(false)

  val disposed = promise[Unit]

  def live(): Boolean = _disposed.get

  protected def dispose(disposing: Boolean)(implicit ectx: ExecutionContext): Future[Unit] = future(Unit)

  final def dispose()(implicit ectx: ExecutionContext): Future[Unit] = {
    if (!_disposed.getAndSet(true)) {
      disposed.completeWith(dispose(true))
    }

    disposed.future
  }
}
