package org.livefx

import org.livefx.script._

trait LiveBuffer[A] extends Buffer[A] with LiveSeq[A] with Publisher[Message[A] with Undoable] {
  type Pub <: LiveBuffer[A]
  
  abstract override def +=(element: A): this.type = {
    super.+=(element)
    changes.publish(new Include(End, element) with Undoable {
      def undo() { trimEnd(1) }
    })
    this
  }

  abstract override def ++=(xs: TraversableOnce[A]): this.type = {
    for (x <- xs) this += x
    this
  }

  abstract override def +=:(element: A): this.type = {
    super.+=:(element)
    changes.publish(new Include(Start, element) with Undoable {
      def undo() { trimStart(1) }
    })
    this
  }

  abstract override def update(n: Int, newelement: A): Unit = {
    val oldelement = apply(n)
    super.update(n, newelement)
    changes.publish(new Update(Index(n), newelement, oldelement) with Undoable {
      def undo() { update(n, oldelement) }
    })
  }

  abstract override def remove(n: Int): A = {
    val oldelement = apply(n)
    super.remove(n)
    changes.publish(new Remove(Index(n), oldelement) with Undoable {
      def undo() { insert(n, oldelement) }
    })
    oldelement
  }

  abstract override def clear(): Unit = {
    super.clear
    changes.publish(new Reset with Undoable {
      def undo() { throw new UnsupportedOperationException("cannot undo") }
    })
  }

  abstract override def insertAll(n: Int, elems: scala.collection.Traversable[A]) {
    super.insertAll(n, elems)
    var curr = n - 1
    val msg = elems.foldLeft(new Script[A]() with Undoable {
      def undo() { throw new UnsupportedOperationException("cannot undo") }
    }) {
      case (msg, elem) =>
        curr += 1
        msg += Include(Index(curr), elem)
    }
    changes.publish(msg)
  }
}
