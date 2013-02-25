package org.livefx

import scala.collection.mutable._
import org.livefx.script._

trait LiveMap[A, B] extends Map[A, B] with Publisher[Message[(A, B)] with Undoable] {
  type Pub <: LiveMap[A, B]

  abstract override def += (kv: (A, B)): this.type = {
    val (key, value) = kv

    get(key) match {
      case None =>
        super.+=(kv)
        publish(new Include((key, value)) with Undoable {
          def undo = -=(key)
        })
      case Some(old) =>
        super.+=(kv)
        publish(new Update((key, value), (key, old)) with Undoable {
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
        publish(new Remove((key, old)) with Undoable {
          def undo = update(key, old)
        })
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
