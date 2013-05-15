package org.livefx

import org.livefx.script.Update
import org.livefx.script.NoLo
import org.livefx.script.Spoil

abstract class LiveBinding[A] extends LiveValue[A] with Unspoilable {
  type Pub <: LiveBinding[A]

  private lazy val _spoils = new EventSource[Pub, Spoil](publisher)
  
  protected override def spoilsSource: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Pub, Spoil] = _spoils

  lazy val _updates = new EventSource[Pub, Update[A]](publisher) {
    lazy val spoilHandler: (Any, Any) => Unit = { (_, _) => LiveBinding.this.value }
    
    override def subscribe(subscriber: (Pub, Update[A]) => Unit): Disposable = {
      LiveBinding.this.spoils.subscribe(spoilHandler)
      LiveBinding.this.value
      super.subscribe(subscriber)
    }
  
    override def subscribeWeak(subscriber: (Pub, Update[A]) => Unit): Disposable ={
      LiveBinding.this.spoils.subscribeWeak(spoilHandler)
      LiveBinding.this.value
      super.subscribeWeak(subscriber)
    }
  
    override def unsubscribe(subscriber: (Pub, Update[A]) => Unit): Unit = {
      super.unsubscribe(subscriber)
      if (isEmpty) LiveBinding.this.spoils.unsubscribe(spoilHandler)
    }
  }

  override def updates: Events[Pub, Update[A]] = _updates

  private var _value: A = null.asInstanceOf[A]
  
  override def value: A = {
    if (spoiled) {
      val oldValue = _value
      val newValue = computeValue
      _value = newValue
      unspoil()
      _updates.publish(Update(NoLo, oldValue, newValue))
    }
    
    _value
  }
  
  protected def computeValue: A
}
