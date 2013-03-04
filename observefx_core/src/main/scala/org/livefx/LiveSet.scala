package org.livefx

import org.livefx.script._

trait LiveSet[A] extends Set[A] with Changeable[A, Change[A]] {
  type Pub <: LiveSet[A]

  abstract override def +=(elem: A): this.type = {
    if (!contains(elem)) {
      super.+=(elem)
      changesSink.publish(Include(NoLo, elem))
    }
    this
  }

  abstract override def -=(elem: A): this.type = {
    if (contains(elem)) {
      super.-=(elem)
      changesSink.publish(Remove(NoLo, elem))
    }
    this
  }

  abstract override def clear(): Unit = {
    super.clear
    changesSink.publish(Reset)
  }
}
