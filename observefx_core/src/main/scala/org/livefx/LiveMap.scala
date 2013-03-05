package org.livefx

import org.livefx.script._

trait LiveMap[A, B] extends Map[A, B] with Changeable[(A, B), Change[(A, B)]] {
  type Pub <: LiveMap[A, B]
  
  abstract override def += (kv: (A, B)): this.type = {
    val (key, value) = kv

    get(key) match {
      case None =>
        super.+=(kv)
        changesSink.publish(Include(NoLo, kv))
      case Some(old) =>
        super.+=(kv)
        changesSink.publish(Update(NoLo, kv, (key, old)))
    }
    this
  }

  abstract override def -= (key: A): this.type = {
    get(key) match {
      case None =>
      case Some(old) =>
        super.-=(key)
        changesSink.publish(Remove(NoLo, (key, old)))
    }
    this
  }

  abstract override def clear(): Unit = {
    super.clear
    changesSink.publish(Reset)
  }
}
