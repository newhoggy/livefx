package org.livefx

import java.util.{ArrayList => JArrayList}
import java.util.{Collection => JCollection}
import java.util.{HashMap => JHashMap}
import java.util.{HashSet => JHashSet}
import java.util.{Iterator => JIterator}
import javafx.beans.InvalidationListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import scala.collection.JavaConversions._
import scala.collection.immutable.HashSet

package object jfx {
  implicit class RichLiveIterable[T](self: Live[Iterable[T]]) {
    def asObservableArrayList: ObservableList[T] = {
      lazy val targetList: ObservableList[T] = FXCollections.observableArrayList(new JArrayList[T] {
        val binding = for {
          iterable <- self
        } yield targetList.setAll(iterable.seq)
      })

      return FXCollections.unmodifiableObservableList(targetList)
    }
  }

  implicit class RichLiveMap[A, B](self: Live[Map[A, B]]) {
    def asObservableHashMap: ObservableMap[A, B] = {
      lazy val targetMap: ObservableMap[A, B] = FXCollections.observableMap(new JHashMap[A, B] {
        var oldMap = self.value
        val binding = for {
          newMap <- self
        } yield {
          for (k <- oldMap.keySet) {
            if (!newMap.contains(k)) {
              targetMap.remove(k)
            }
          }
  
          for ((k, v) <- newMap) {
            newMap.get(k) match {
              case Some(value) if value == v => // Do nothing
              case _ => targetMap.put(k, v)
            }
          }
        }
      })
      
      return FXCollections.unmodifiableObservableMap(targetMap)
    }
  }

  implicit class RichLiveSet[A](self: Live[Set[A]]) {
    def asObservableHashSet: ObservableSet[A] = {
      lazy val targetSet: ObservableSet[A] = FXCollections.observableSet(new JHashSet[A] {
        var oldSet = self.value
        val binding = for {
          newSet <- self
        } yield {
          for (e <- newSet -- oldSet) {
            targetSet.remove(e)
          }
          
          for (e <- oldSet -- newSet) {
            targetSet.add(e)
          }
        }
      })
      
      return unmodifiableObservableSet(targetSet)
    }
  }

  def unmodifiableObservableSet[A](observableSet: ObservableSet[A]): ObservableSet[A] = new UnmodifiableObservableSet(observableSet)

  def unmodifiableIterator[A](iterator: JIterator[A]): JIterator[A] = new JIterator[A] {
    override def hasNext(): Boolean = iterator.hasNext()
    override def next(): A = iterator.next()
    override def remove(): Unit = throw new UnsupportedOperationException
  }
}
