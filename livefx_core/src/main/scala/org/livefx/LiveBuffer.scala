package org.livefx

import org.livefx.script._

trait LiveBuffer[A] extends Buffer[A] with LiveSeq[A] with Changeable[A, Change[A]] {
  type Pub <: LiveBuffer[A]
  
  abstract override def +=(element: A): this.type = {
    super.+=(element)
    changesSink.publish(Include(End, element))
    this
  }

  abstract override def ++=(xs: TraversableOnce[A]): this.type = {
    for (x <- xs) this += x
    this
  }

  abstract override def +=:(element: A): this.type = {
    super.+=:(element)
    changesSink.publish(Include(Start, element))
    this
  }

  abstract override def update(n: Int, newelement: A): Unit = {
    val oldelement = apply(n)
    super.update(n, newelement)
    changesSink.publish(Update(Index(n), newelement, oldelement))
  }

  abstract override def remove(n: Int): A = {
    val oldelement = apply(n)
    super.remove(n)
    changesSink.publish(Remove(Index(n), oldelement))
    oldelement
  }

  abstract override def clear(): Unit = {
    super.clear
    changesSink.publish(Reset)
  }

  abstract override def insertAll(n: Int, elems: scala.collection.Traversable[A]) {
    super.insertAll(n, elems)
    var curr = n - 1
    val msg = elems.foldLeft(new Script[A]()) {
      case (msg, elem) =>
        curr += 1
        msg += Include(Index(curr), elem)
    }
    changesSink.publish(msg)
  }
}