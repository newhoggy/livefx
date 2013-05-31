package org.livefx.jfx

import java.util.{ArrayList => JArrayList}
import java.util.{Collection => JCollection}
import java.util.{HashMap => JHashMap}
import java.util.{HashSet => JHashSet}
import java.util.{Iterator => JIterator}
import java.util.{List => JList}
import java.util.{ListIterator => JListIterator}
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.WeakInvalidationListener
import javafx.beans.binding.{Binding => JBinding}
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.IntegerProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.BooleanProperty
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableIntegerValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import javafx.collections.ListChangeListener
import javafx.collections.MapChangeListener
import javafx.collections.SetChangeListener
import javafx.collections.WeakListChangeListener
import javafx.collections.WeakMapChangeListener
import javafx.collections.WeakSetChangeListener
import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import scala.collection.JavaConversions._
import scala.ref.WeakReference
import org.livefx.Live
import org.livefx.Binding
import org.livefx.util.TidyReferenceQueue
import org.livefx.util.TidyWeakReference

object Beans {
  object Implicits {
    implicit class RichObservable[A](self: Observable) {
      private final def hotListener: HotInvalidationListener = {
        val listener = new HotInvalidationListener
        self.removeListener(listener)
        self.addListener(listener)
        listener
      }

      final def store: PropertyStore[Any] = hotListener.store
      final def storeFor[T]: PropertyStore[T] = store.asInstanceOf[PropertyStore[T]]
      final def properties: HashMap[Any, Any] = store.properties
      final def properties_=(value: HashMap[Any, Any]): Unit = store.properties = value
      final def cache[T <: AnyRef](key: HotKey[T])(f: => T): T = {
        TidyReferenceQueue.tidy()
        val store = self.storeFor[WeakReference[T]]
        store.properties.get(key) match {
          case Some(WeakReference(value)) => value
          case None => {
            val value = f
            val weakRef = new TidyWeakReference[T](value, TidyReferenceQueue) {
              override def dispose(): Unit = store.properties -= key
            }
            store.properties += (key -> weakRef)
            value
          }
        }
      }
    }

    implicit class RichProperty[A](self: Property[A]) {
      final def <==(value: A): Unit = self.setValue(value)
      final def value: A = self.getValue()
    }

    implicit class RichIntegerProperty(self: IntegerProperty) {
      final def <==(value: Int): Unit = self.setValue(value)
      final def value: Int = self.getValue()
    }

    implicit class RichDoubleProperty(self: DoubleProperty) {
      final def <==(value: Double): Unit = self.setValue(value)
      final def value: Double = self.getValue()
    }

    implicit class RichBooleanProperty(self: BooleanProperty) {
      final def <==(value: Boolean): Unit = self.setValue(value)
      final def value: Boolean = self.getValue()
    }

    private object RichObservableValueKey extends HotKey[Nothing]
    implicit class RichObservableValue[A](self: ObservableValue[A]) {
      final def value: A = self.getValue()
      final def live: Live[A] = self.cache(RichObservableValueKey: HotKey[Binding[A]]) {
        new Binding[A] with JfxBindable {
          bind(self)

          override def computeValue: A = self.value
        }
      }
    }

    private object RichObservableIntegerValueKey extends HotKey[Binding[Int]]
    implicit class RichObservableIntegerValue[A](self: ObservableIntegerValue) {
      final def value: Int = self.get()
      final def live: Live[Int] = self.cache(RichObservableIntegerValueKey) {
        new Binding[Int] with JfxBindable {
          bind(self)
  
          override def computeValue: Int = self.value
        }
      }
    }

    private object RichObservableDoubleValueKey extends HotKey[Binding[Double]]
    implicit class RichObservableDoubleValue[A](self: ObservableDoubleValue) {
      def value: Double = self.get()
      def live: Live[Double] = self.cache(RichObservableDoubleValueKey) {
        new Binding[Double] with JfxBindable {
          bind(self)

          override def computeValue: Double = self.value
        }
      }
    }

    private object RichObservableBooleanValueKey extends HotKey[Binding[Boolean]]
    implicit class RichObservableBooleanValue[A](self: ObservableBooleanValue) {
      def value: Boolean = self.get()
      def live: Live[Boolean] = self.cache(RichObservableBooleanValueKey) {
       new Binding[Boolean] with JfxBindable {
          bind(self)
  
          override def computeValue: Boolean = self.value
        }
      }
    }

    implicit class RichInvalidationListener(self: InvalidationListener) {
      def weak: WeakInvalidationListener = new WeakInvalidationListener(self)
    }

    implicit class RichChangeListener[A](self: ChangeListener[A]) {
      def weak: WeakChangeListener[A] = new WeakChangeListener[A](self)
    }

    implicit class RichListChangeListener[A](self: ListChangeListener[A]) {
      def weak: WeakListChangeListener[A] = new WeakListChangeListener[A](self)
    }

    implicit class RichMapChangeListener[A, B](self: MapChangeListener[A, B]) {
      def weak: WeakMapChangeListener[A, B] = new WeakMapChangeListener[A, B](self)
    }

    implicit class RichSetChangeListener[A](self: SetChangeListener[A]) {
      def weak: WeakSetChangeListener[A] = new WeakSetChangeListener[A](self)
    }

    implicit class RichLiveSeq[T](self: Live[Seq[T]]) {
      def asObservableArrayList: ObservableList[T] = new ObservableList[T] { target =>
        private var invalidationListeners = HashSet.empty[WeakReference[InvalidationListener]]
        private var listChangeListeners = HashSet.empty[WeakReference[ListChangeListener[_ >: T]]]
        private val binding = new Binding[ObservableList[T]] {
          var oldValue: Seq[T] = Nil
          val underlying = FXCollections.observableArrayList[T]
          private val ref = self.spoils.subscribe { (p, e) =>
            for (weakListener <- invalidationListeners) {
              weakListener match {
                case WeakReference(listener) => listener.invalidated(target)
              }
            }
            for (weakListener <- listChangeListeners) {
              weakListener match {
                case WeakReference(listener) => listener.onChanged(new ListChangeListener.Change(target) {
                  var more = true
                  override def getFrom(): Int = 0
                  override def getPermutation(): Array[Int] = Array.empty
                  override def getRemoved(): JList[T] = oldValue
                  override def getTo(): Int = value.size
                  override def next(): Boolean = try { value; more } finally { more = false }
                  override def reset(): Unit = more = true
                  override def wasPermutated(): Boolean = false
                  override def wasAdded(): Boolean = value.size != 0
                  override def wasRemoved(): Boolean = oldValue.size != 0
                  override def getAddedSubList(): JList[T] = value
                  override def getRemovedSize(): Int = oldValue.size
                  override def getAddedSize(): Int = value.size
                })
              }
            }
          }
          underlying.setAll(self.value)
          override def computeValue: ObservableList[T] = {
            oldValue = self.value
            underlying.setAll(self.value)
            underlying
          }
        }
        override def add(index: Int, value: T): Unit = throw new UnsupportedOperationException
        override def add(index: T): Boolean = throw new UnsupportedOperationException
        override def addAll(index: Int, values: JCollection[_ <: T]): Boolean = throw new UnsupportedOperationException
        override def addAll(values: JCollection[_ <: T]): Boolean = throw new UnsupportedOperationException
        override def clear(): Unit = throw new UnsupportedOperationException
        override def contains(value: Any): Boolean = binding.value.contains(value)
        override def containsAll(values: JCollection[_]): Boolean = binding.value.containsAll(values)
        override def get(index: Int): T = binding.value.get(index)
        override def indexOf(value: Any): Int = binding.value.indexOf(value)
        override def isEmpty(): Boolean = binding.value.isEmpty
        override def iterator(): JIterator[T] = binding.value.iterator
        override def lastIndexOf(value: Any): Int = binding.value.lastIndexOf(value)
        override def listIterator(index: Int): JListIterator[T] = binding.value.listIterator
        override def listIterator(): JListIterator[T] = binding.value.listIterator
        override def remove(index: Int): T = throw new UnsupportedOperationException
        override def remove(value: Any): Boolean = throw new UnsupportedOperationException
        override def removeAll(values: JCollection[_]): Boolean = throw new UnsupportedOperationException
        override def retainAll(values: JCollection[_]): Boolean = throw new UnsupportedOperationException
        override def set(index: Int, value: T): T = throw new UnsupportedOperationException
        override def size(): Int = binding.value.size
        override def subList(from: Int, to: Int): JList[T] = binding.value.subList(from, to)
        override def toArray[T](array: Array[T with Object]): Array[T with Object] = binding.value.toArray[T](array)
        override def toArray(): Array[Object] = binding.value.toArray()
        override def addAll(index: T*): Boolean = throw new UnsupportedOperationException
        override def remove(from: Int, to: Int): Unit = throw new UnsupportedOperationException
        override def removeAll(values: T*): Boolean = throw new UnsupportedOperationException
        override def retainAll(values: T*): Boolean = throw new UnsupportedOperationException
        override def setAll(values: JCollection[_ <: T]): Boolean = throw new UnsupportedOperationException
        override def setAll(values: T*): Boolean = throw new UnsupportedOperationException
        override def addListener(listener: InvalidationListener): Unit = {
          TidyReferenceQueue.tidy()
          invalidationListeners += new TidyWeakReference(listener, TidyReferenceQueue) {
            override def dispose(): Unit = invalidationListeners -= this
          }
        }
        override def removeListener(listener: InvalidationListener): Unit = {
          TidyReferenceQueue.tidy()
          invalidationListeners -= new TidyWeakReference(listener, TidyReferenceQueue) {
            override def dispose(): Unit = invalidationListeners -= this
          }
        }
        override def addListener(listener: ListChangeListener[_ >: T]): Unit = {
          TidyReferenceQueue.tidy()
          listChangeListeners += new TidyWeakReference[ListChangeListener[_ >: T]](listener, TidyReferenceQueue) {
            override def dispose(): Unit = listChangeListeners -= this
          }
        }
        override def removeListener(listener: ListChangeListener[_ >: T]): Unit = {
          TidyReferenceQueue.tidy()
          listChangeListeners -= new TidyWeakReference[ListChangeListener[_ >: T]](listener, TidyReferenceQueue) {
            override def dispose(): Unit = listChangeListeners -= this
          }
        }
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
  }

  def unmodifiableIterator[A](iterator: JIterator[A]): JIterator[A] = new JIterator[A] {
    override def hasNext(): Boolean = iterator.hasNext()
    override def next(): A = iterator.next()
    override def remove(): Unit = throw new UnsupportedOperationException
  }

  def unmodifiableObservableSet[A](observableSet: ObservableSet[A]): ObservableSet[A] = new UnmodifiableObservableSet(observableSet)
}
