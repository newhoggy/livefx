package org.livefx

import scala.concurrent._

class Disposer extends Disposable {
  private var lock = new Object
  private var disposables = List[Disposable]()
  
  def disposes[D <: Disposable](disposable: D): D = {
    lock.synchronized {
      disposables = disposable :: disposables
    }
    disposable
  }
  
  def takeDisposables = lock.synchronized {
      val mydisposables = disposables
      disposables = Nil
      mydisposables
    }
  
  override def dispose()(implicit ectx: ExecutionContext): Future[Unit] = {
    takeDisposables.foldLeft(future {}) { (f, disposable) =>
      for {
        _ <- f
        _ <- disposable.dispose()
      } yield Unit
    }
  }
}
