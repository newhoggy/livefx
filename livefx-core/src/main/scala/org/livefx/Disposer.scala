package org.livefx

class Disposer extends Disposable {
  private val lock = new Object
  private var disposables = List.empty[Disposable]
  
  def disposes[D <: Disposable](disposable: D): D = {
    // TODO: Make lock-free
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

  override def onDispose(): Unit = takeDisposables.foreach(_.dispose())
}
