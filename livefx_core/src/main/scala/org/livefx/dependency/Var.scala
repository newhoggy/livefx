package org.livefx.dependency

import org.livefx.script.Change
import org.livefx.script.Spoil
import java.util.concurrent.atomic.AtomicReference
import scala.collection.immutable.HashSet
import scalaz.concurrent.Atomic
import org.livefx.{dependency => dep}

trait Var[@specialized(Boolean, Int, Long, Double) A] extends Live[A] {
  private lazy val _spoils = new EventSource[Spoil] {
    def dependency: dep.Live[Int] = dep.Independent
  }

  protected override def spoilSink: EventSink[Spoil] = _spoils

  override def spoils: Events[Spoil] = _spoils

  lazy val _changes = new EventSource[Change[A]] {
    def dependency: dep.Live[Int] = dep.Independent
  }

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
      spoil(Spoil())
      _changes.publish(Change(oldValue, newValue))
    }
  }
}
