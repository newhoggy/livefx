package org.livefx

import org.livefx.script._

trait LiveSet[A] extends Set[A] with Publisher {
  type Pub <: LiveSet[A]

  protected lazy val changesSink = new EventSource[Pub, Change[A]](publisher)
  
  def changes: Events[Pub, Change[A]] = changesSink

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
