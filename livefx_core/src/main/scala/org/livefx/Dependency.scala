package org.livefx

trait Dependency extends Disposable

object Dependency {
  val None = new Dependency with Disposed
}
