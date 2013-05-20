package org.livefx

import org.livefx.trees.indexed._
import org.livefx.script._
import org.livefx.util.Memoize
import scalaz.Monoid

class VarTreeSeq[A](_value: Tree[A] = Leaf) extends Var[Tree[A]](_value) with LiveTreeSeq[A] {
  type Pub <: VarTreeSeq[A]
  
  override def asLiveValue: LiveValue[Tree[A]] = this
  
  private lazy val _spoils = new EventSource[Pub, Spoil](publisher)
  
  protected override def spoilsSource: EventSink[Spoil] = _spoils
  
  override def spoils: Events[Pub, Spoil] = _spoils

  lazy val _changes = new EventSource[Pub, Change[A]](publisher)

  def changes: Events[Pub, Change[A]] = _changes

  override def value_=(newValue: Tree[A]): Unit = {
    updateValue(_value, newValue)
    var curr = -1
    val msg = newValue.toList.foldLeft(new Script[A]() += Reset) {
      case (msg, elem) =>
        curr += 1
        msg += Include(Start(curr), elem)
    }
    _changes.publish(msg)
  }
  
  def +=(element: A): this.type = {
    updateValue(_value, this.value.insert(this.value.size, element))
    _changes.publish(Include(End(0), element))
    this
  }

  def ++=(xs: TraversableOnce[A]): this.type = {
    for (x <- xs) this += x
    this
  }

  def +=:(element: A): this.type = {
    updateValue(_value, this.value.insert(0, element))
    _changes.publish(Include(Start(0), element))
    this
  }

  def apply(index: Int): A = this.value.lookup(index).value

  def update(index: Int, newElement: A): Unit = {
    val oldElement = apply(index)
    updateValue(_value, this.value.update(index, newElement))
    _changes.publish(Update(Start(index), newElement, oldElement))
  }

  def remove(index: Int): A = {
    val oldElement = apply(index)
    updateValue(_value, this.value.delete(index))
    _changes.publish(Remove(Start(index), oldElement))
    oldElement
  }

  def clear(): Unit = {
    updateValue(_value, Leaf)
    _changes.publish(Reset)
  }

  def insert(index: Int, elem: A): Unit = {
    updateValue(_value, this.value.insert(index, elem))
    _changes.publish(Include(Start(index), elem))
  }

  def insertAll(index: Int, elems: scala.collection.Traversable[A]): Unit = {
    updateValue(_value, elems.foldRight(this.value)((e, t) => t.insert(index, e)))
    
    var curr = index - 1
    val msg = elems.foldLeft(new Script[A]()) {
      case (msg, elem) =>
        curr += 1
        msg += Include(Start(curr), elem)
    }
    _changes.publish(msg)
  }
  
  override def toString(): String = "VarSeq(" + _value.mkString("Tree(", ", ", ")") + ")"
}