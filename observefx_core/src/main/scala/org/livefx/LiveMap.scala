package org.livefx

import org.livefx.script._

trait LiveMap[A, B] extends Map[A, B] {
  lazy val changes = new EventSource[LiveMap[A, B], Message[(A, B)] with Undoable](this)

  abstract override def += (kv: (A, B)): this.type = {
    val (key, value) = kv

    get(key) match {
      case None =>
        super.+=(kv)
        changes.publish(new Include((key, value)) with Undoable {
          def undo = -=(key)
        })
      case Some(old) =>
        super.+=(kv)
        changes.publish(new Update((key, value), (key, old)) with Undoable {
          def undo = +=((key, old))
        })
    }
    this
  }

  abstract override def -= (key: A): this.type = {
    get(key) match {
      case None =>
      case Some(old) =>
        super.-=(key)
        changes.publish(new Remove((key, old)) with Undoable {
          def undo = update(key, old)
        })
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
