package org.livefx

import scala.collection.mutable.Set
import scala.collection.mutable.Undoable
import org.livefx.script._

trait LiveSet[A] extends Set[A] with Publisher[Message[A] with Undoable] {
  type Pub <: LiveSet[A]

  abstract override def +=(elem: A): this.type = {
    if (!contains(elem)) {
      super.+=(elem)
      publish(new Include(elem) with Undoable { def undo = -=(elem) })
    }
    this
  }

  abstract override def -=(elem: A): this.type = {
    if (contains(elem)) {
      super.-=(elem)
      publish(new Remove(elem) with Undoable { def undo = +=(elem) })
    }
    this
  }

  abstract override def clear(): Unit = {
    super.clear
    publish(new Reset with Undoable {
      def undo(): Unit = throw new UnsupportedOperationException("cannot undo")
    })
  }
}
