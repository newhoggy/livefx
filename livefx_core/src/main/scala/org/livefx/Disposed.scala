package org.livefx

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

trait Disposed extends Disposable {
  dispose()
} 

object Disposed extends Disposed
