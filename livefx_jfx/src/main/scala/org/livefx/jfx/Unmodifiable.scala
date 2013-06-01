package org.livefx.jfx

import java.util.{Iterator => JIterator}

object Unmodifiable {
  def apply[A](iterator: JIterator[A]): JIterator[A] = new JIterator[A] {
    override def hasNext(): Boolean = iterator.hasNext()
    override def next(): A = iterator.next()
    override def remove(): Unit = throw new UnsupportedOperationException
  }
}
