package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil
import org.livefx.{dependency => dep}

trait Binding[A] extends Live[A] with Unspoilable { self =>
  private lazy val _spoils = new EventSource[Spoil]
  
  protected override def spoilSink: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Spoil] = _spoils

  lazy val _changes = new EventSource[Change[A]] {
    lazy val spoilHandler: Any => Unit = { _ => self.value }
    var subscription: Disposable = Disposed

    override def subscribe(subscriber: Change[A] => Unit): Disposable = {
      subscription = self.spoils.subscribe(spoilHandler)
      self.value
      super.subscribe(subscriber)
    }
  }

  override def changes: Events[Change[A]] = _changes

  private var _value: A = null.asInstanceOf[A]
  
  override def value: A = {
    if (spoiled) {
      val oldValue = _value
      val newValue = computeValue
      _value = newValue
      unspoil()
      _changes.publish(Change(oldValue, newValue))
    }
    
    _value
  }

  protected def computeValue: A
}
