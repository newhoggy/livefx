package org.livefx

import org.livefx.script._

trait LiveMap[A, B] extends Map[A, B] {
  lazy val changes = new EventSource[LiveMap[A, B], Message[(A, B)]](this)

  abstract override def += (kv: (A, B)): this.type = {
    val (key, value) = kv

    get(key) match {
      case None =>
        super.+=(kv)
        changes.publish(Include(NoLo, (key, value)))
      case Some(old) =>
        super.+=(kv)
        changes.publish(Update(NoLo, (key, value), (key, old)))
    }
    this
  }

  abstract override def -= (key: A): this.type = {
    get(key) match {
      case None =>
      case Some(old) =>
        super.-=(key)
        changes.publish(Remove(NoLo, (key, old)))
    }
    this
  }

  abstract override def clear(): Unit = {
    super.clear
    changes.publish(Reset)
  }
}
