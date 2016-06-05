package org.livefx

import java.io.Closeable

import org.livefx.core.disposal.Closed
import org.livefx.script.{Change, Spoil}

trait Binding[A] extends Live[A] with Unspoilable { self =>
  private lazy val _spoils = new EventBus[Spoil]
  
  protected override def spoilSink: EventSink[Spoil] = _spoils
  
  override def spoils: EventSource[Spoil] = _spoils

  lazy val _changes = new EventBus[Change[A]] {
    lazy val spoilHandler: Any => Unit = { _ => self.value }
    var subscription: AutoCloseable = Closed

    override def subscribe(subscriber: Change[A] => Unit): Closeable = {
      subscription = self.spoils.subscribe(spoilHandler)
      self.value
      super.subscribe(subscriber)
    }

    override def close(): Unit = {
      subscription.close()
    }
  }

  override def changes: EventSource[Change[A]] = _changes

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
