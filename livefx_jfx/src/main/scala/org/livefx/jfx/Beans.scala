package org.livefx.jfx

import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.binding.{Binding => JBinding}
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.Property
import javafx.beans.property.IntegerProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.BooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.WeakChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableDoubleValue
import javafx.collections.ListChangeListener
import javafx.collections.WeakListChangeListener
import javafx.collections.MapChangeListener
import javafx.collections.WeakMapChangeListener
import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener
import org.livefx.Live
import org.livefx.Binding
import javafx.beans.binding.BooleanBinding
import javafx.beans.Observable
import scala.collection.immutable.HashMap
import scala.ref.ReferenceQueue
import scala.ref.WeakReference
import scala.annotation.tailrec

object Beans {
  object Implicits {
    private object StoreRefQueue extends ReferenceQueue[Nothing] {
      @tailrec
      def tidyUp(): Unit = poll match {
        case Some(weakRef) => weakRef match {
          case runnable: Runnable => runnable.run(); tidyUp()
          case _ =>
        }
        case None =>
      }
    }

    implicit class RichObservable[A](self: Observable) {
      private final def hotListener: HotInvalidationListener = {
        val listener = new HotInvalidationListener
        self.addListener(listener)
        self.removeListener(listener)
        listener
      }

      final def store: PropertyStore[Any] = hotListener.store
      final def storeFor[T]: PropertyStore[T] = store.asInstanceOf[PropertyStore[T]]
      final def properties: HashMap[Any, Any] = store.properties
      final def properties_=(value: HashMap[Any, Any]): Unit = store.properties = value
      final def cache[T <: AnyRef](key: HotKey[T])(f: => T): T = {
        StoreRefQueue.tidyUp()
        val store = self.storeFor[WeakReference[T]]
        store.properties.get(key) match {
          case Some(WeakReference(value)) => value
          case None => {
            val value = f
            val weakRef = new WeakReference[T](value, StoreRefQueue) with Runnable {
              override def run(): Unit = store.properties -= key
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
  }
}
