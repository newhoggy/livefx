package org.livefx.jfx

import scala.collection.immutable.HashSet

import java.util.{Collection => JCollection}
import java.util.{Iterator => JIterator}
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import javafx.beans.InvalidationListener

class UnmodifiableObservableSet[A](observableSet: ObservableSet[A]) extends ObservableSet[A] {
  var listeners = HashSet[InvalidationListener]()
  var changeListeners = HashSet[SetChangeListener[_ >: A]]()
  override def addListener(listener: InvalidationListener): Unit = listeners += listener
  override def removeListener(listener: InvalidationListener): Unit = listeners -= listener
  override def addListener(listener: SetChangeListener[_ >: A]): Unit = changeListeners += listener
  override def removeListener(listener: SetChangeListener[_ >: A]): Unit = changeListeners -= listener
  override def add(value: A): Boolean = throw new UnsupportedOperationException
  override def addAll(collection: JCollection[_ <: A]): Boolean = throw new UnsupportedOperationException
  override def clear(): Unit = throw new UnsupportedOperationException
  override def contains(value: Any): Boolean = observableSet.contains(value)
  override def containsAll(collection: java.util.Collection[_]): Boolean = observableSet.containsAll(collection)
  override def isEmpty(): Boolean = observableSet.isEmpty()
  override def iterator(): JIterator[A] = Beans.unmodifiableIterator(observableSet.iterator())
  override def remove(x$1: Any): Boolean = throw new UnsupportedOperationException
  override def removeAll(values: JCollection[_]): Boolean = throw new UnsupportedOperationException
  override def retainAll(values: JCollection[_]): Boolean = throw new UnsupportedOperationException
  override def size(): Int = observableSet.size()
  override def toArray[T](array: Array[T with Object]): Array[T with Object] = observableSet.toArray[T](array)
  override def toArray(): Array[Object] = observableSet.toArray()
}
