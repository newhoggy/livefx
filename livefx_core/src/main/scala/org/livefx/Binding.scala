package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil

abstract class Binding[A] extends Live[A] with Unspoilable {
  private lazy val _spoils = new EventSource[Spoil]
  
  protected override def spoilsSource: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Spoil] = _spoils

  lazy val _changes = new EventSource[Change[A]] {
    lazy val spoilHandler: Any => Unit = { _ => Binding.this.value }

    override def subscribe(subscriber: Change[A] => Unit): Disposable = {
      Binding.this.spoils.subscribe(spoilHandler)
      Binding.this.value
      super.subscribe(subscriber)
    }

    override def subscribeWeak(subscriber: Change[A] => Unit): Disposable ={
      Binding.this.spoils.subscribeWeak(spoilHandler)
      Binding.this.value
      super.subscribeWeak(subscriber)
    }
  
    override def unsubscribe(subscriber: Change[A] => Unit): Unit = {
      super.unsubscribe(subscriber)
      if (isEmpty) Binding.this.spoils.unsubscribe(spoilHandler)
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
      _changes.publish(Change(oldValue, newValue))(PublishingStrategy.depthFirst)
    }
    
    _value
  }

  protected def computeValue: A
}
