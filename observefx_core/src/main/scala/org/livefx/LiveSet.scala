package org.livefx

import org.livefx.script._

trait LiveSet[A] extends Set[A] with Changeable[A, Change[A]] {
  type Pub <: LiveSet[A]

  abstract override def +=(elem: A): this.type = {
    if (!contains(elem)) {
      super.+=(elem)
      changesSink.publish(new Include(elem) with Undoable { def undo = -=(elem) })
    }
    this
  }

  abstract override def -=(elem: A): this.type = {
    if (contains(elem)) {
      super.-=(elem)
      changesSink.publish(new Remove(elem) with Undoable { def undo = +=(elem) })
    }
    this
  }

  abstract override def clear(): Unit = {
    super.clear
    changesSink.publish(new Reset with Undoable {
      def undo(): Unit = throw new UnsupportedOperationException("cannot undo")
    })
  }
}
