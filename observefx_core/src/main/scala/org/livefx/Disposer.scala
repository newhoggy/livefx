package org.livefx

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
  
  def dispose(): Unit = for (disposable <- takeDisposables) disposable.dispose()
}
