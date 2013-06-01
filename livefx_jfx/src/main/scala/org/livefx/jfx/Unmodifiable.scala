package org.livefx.jfx

import java.util.{Iterator => JIterator}
import javafx.collections.ObservableSet

object Unmodifiable {
  def apply[A](iterator: JIterator[A]): JIterator[A] = new JIterator[A] {
    override def hasNext(): Boolean = iterator.hasNext()
    override def next(): A = iterator.next()
    override def remove(): Unit = throw new UnsupportedOperationException
  }

  def apply[A](observableSet: ObservableSet[A]): ObservableSet[A] = new UnmodifiableObservableSet(observableSet)
}
