package org.livefx

import org.livefx.script.Change
import org.livefx.script.Spoil
import java.util.concurrent.atomic.AtomicReference
import scala.collection.immutable.HashSet

trait Var[@specialized(Boolean, Int, Long, Double) A] extends Live[A] {
  type PublishingStrategy[A] = DepthFirst[A]

  private lazy val _spoils = new EventSource[Spoil] with PublishingStrategy[Spoil]

  protected override def spoilsSource: EventSink[Spoil] = _spoils

  override def spoils: Events[Spoil] = _spoils

  lazy val _changes = new EventSource[Change[A]] with PublishingStrategy[Change[A]]

  override def changes: Events[Change[A]] = _changes

  def value: A

  def value_=(newValue: A): Unit

  def :=(value: A): this.type = {
    this.value = value
    this
  }
}

object Var {
  def apply[@specialized(Boolean, Int, Long, Double) A](initialValue: A): Var[A] = new Var[A] {
    var _value: A = initialValue

    override def value: A = _value

    override def value_=(newValue: A): Unit = updateValue(_value, newValue)

    protected def updateValue(oldValue: A, newValue: A): Unit = {
      _value = newValue
      spoil(Spoil(new AtomicReference(HashSet.empty[Spoilable])))
      _changes.publish(Change(oldValue, newValue))
    }
  }
}
