package org.livefx

import org.livefx.trees.indexed._
import org.livefx.script._
import org.livefx.util.Memoize
import scalaz.Monoid

class VarSeq[A](private var _value: Tree[A] = Leaf) extends LiveValue[Tree[A]] {
  type Pub <: VarSeq[A]
  
  lazy val _updates = new EventSource[Pub, Update[Tree[A]]](publisher)

  override def updates: Events[Pub, Update[Tree[A]]] = _updates

  lazy val _changes = new EventSource[Pub, Change[A]](publisher)

  def changes: Events[Pub, Change[A]] = _changes

  def value: Tree[A] = _value

  def value_=(newValue: Tree[A]): Unit = {
    val oldValue = _value
    _value = newValue
    spoil(Spoil())
    _updates.publish(Update(NoLo, oldValue, newValue))
  }

  def +=(element: A): this.type = {
    this.value = this.value.insert(this.value.size, element)
    _changes.publish(Include(End, element))
    this
  }

  def ++=(xs: TraversableOnce[A]): this.type = {
    for (x <- xs) this += x
    this
  }

  def +=:(element: A): this.type = {
    this.value = this.value.insert(0, element)
    _changes.publish(Include(Start, element))
    this
  }

  def apply(index: Int): A = this.value.lookup(index).value

  def update(index: Int, newElement: A): Unit = {
    val oldElement = apply(index)
    this.value = this.value.update(index, newElement)
    _changes.publish(Update(Index(index), newElement, oldElement))
  }

  def remove(index: Int): A = {
    val oldElement = apply(index)
    this.value = this.value.delete(index)
    _changes.publish(Remove(Index(index), oldElement))
    oldElement
  }

  def clear(): Unit = {
    this.value = Leaf
    _changes.publish(Reset)
  }

  def insert(index: Int, elem: A): Unit = {
    this.value = this.value.insert(index, elem)
    _changes.publish(Include(Index(index), elem))
  }

  def insertAll(index: Int, elems: scala.collection.Traversable[A]): Unit = {
    this.value = elems.foldRight(this.value)((e, t) => t.insert(index, e))
    
    var curr = index - 1
    val msg = elems.foldLeft(new Script[A]()) {
      case (msg, elem) =>
        curr += 1
        msg += Include(Index(curr), elem)
    }
    _changes.publish(msg)
  }
  
  override def toString(): String = "VarSeq(" + _value.mkString("Tree(", ", ", ")") + ")"
  
  final def fold(implicit monoid: Monoid[A]): LiveValue[A] = {
    val outer = this
    new LiveBinding[A] {
      val spoilHandler = { (_: Any, spoilEvent: Spoil) =>
        println("--> spoil event")
        spoil(spoilEvent)
      }
      outer.spoils.subscribeWeak(spoilHandler)
      val foldImpl: Tree[A] => A = Memoize.apply(Tree.idOf(_: Tree[A])) { tree =>
        import scalaz.Scalaz._
        tree match {
          case Tree(Leaf, v, Leaf) => v |+| monoid.zero
          case Tree(l, v, Leaf) => foldImpl(l) |+| v |+| monoid.zero
          case Tree(Leaf, v, r) => v |+| foldImpl(r) |+| monoid.zero
          case Tree(l, v, r) => foldImpl(l) |+| v |+| foldImpl(r) |+| monoid.zero
          case Leaf => monoid.zero
        }
      }
      
      override def computeValue: A = foldImpl(outer.value)
    }
  }
}
