package org.livefx

import org.livefx.script._

trait LiveSet[A] extends Set[A] with Changeable[A] {
  type Pub <: LiveSet[A]

  abstract override def +=(elem: A): this.type = {
    if (!contains(elem)) {
      super.+=(elem)
      changes.publish(new Include(elem) with Undoable { def undo = -=(elem) })
    }
    this
  }

  abstract override def -=(elem: A): this.type = {
    if (contains(elem)) {
      super.-=(elem)
      changes.publish(new Remove(elem) with Undoable { def undo = +=(elem) })
    }
    this
  }

  abstract override def clear(): Unit = {
    super.clear
    changes.publish(new Reset with Undoable {
      def undo(): Unit = throw new UnsupportedOperationException("cannot undo")
    })
  }
}
