package org.livefx

import org.livefx.script._

trait LiveMap[A, B] extends Map[A, B] with Changeable[(A, B), Change[(A, B)]] {
  type Pub <: LiveMap[A, B]
  
  override def put(key: A, value: B): Option[B] = {
    super.put(key, value) match {
      case None => changesSink.publish(Include(NoLo, (key, value))); None
      case e@Some(oldValue) => changesSink.publish(Update(NoLo, (key, value), (key, oldValue))); e
    }
  }
  
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
  
  def liveKeys: LiveSet[A] = {
    val source = this
    new HashSet[A] with LiveSet[A] {
      this ++= source.keys
      val target = this
      val ref = source.changes.subscribeWeak { (publisher, change) =>
        def process(pub: LiveMap[A, B], change: Change[(A, B)]): Unit = {
          change match {
            case Remove(location, (oldKey, _)) => target -= oldKey
            case Include(location, (newKey, _)) => target += newKey
            case Update(location, _, _) =>
            case Reset => target.clear()
            case s: Script[(A, B)] => for (e <- s) process(pub, e)
          }
        }
  
        process(publisher, change)
      }
    }
  }
}
