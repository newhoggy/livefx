package org.livefx.jfx

import javafx.beans.value.ObservableValue
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.WeakChangeListener
import javafx.collections.ListChangeListener
import javafx.collections.WeakListChangeListener
import javafx.collections.MapChangeListener
import javafx.collections.WeakMapChangeListener
import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener
import javafx.beans.property.Property
import javafx.beans.property.IntegerProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.BooleanProperty
import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableDoubleValue

object Observables {
  object Implicits {
    implicit class RichProperty[A](self: Property[A]) {
      def <==(value: A): Unit = self.setValue(value)
      def value: A = self.getValue()
    }

    implicit class RichIntegerProperty(self: IntegerProperty) {
      def <==(value: Int): Unit = self.setValue(value)
      def value: Int = self.getValue()
    }

    implicit class RichDoubleProperty(self: DoubleProperty) {
      def <==(value: Double): Unit = self.setValue(value)
      def value: Double = self.getValue()
    }

    implicit class RichBooleanProperty(self: BooleanProperty) {
      def <==(value: Boolean): Unit = self.setValue(value)
      def value: Boolean = self.getValue()
    }

    implicit class RichObservableValue[A](self: ObservableValue[A]) {
      def value: A = self.getValue()
    }

    implicit class RichObservableIntegerValue[A](self: ObservableIntegerValue) {
      def value: Int = self.get()
    }

    implicit class RichObservableDoubleValue[A](self: ObservableDoubleValue) {
      def value: Double = self.get()
    }

    implicit class RichObservableBooleanValue[A](self: ObservableBooleanValue) {
      def value: Boolean = self.get()
    }

    implicit class RichInvalidationListener(self: InvalidationListener) {
      def weak: InvalidationListener = new WeakInvalidationListener(self)
    }

    implicit class RichChangeListener[A](self: ChangeListener[A]) {
      def weak: ChangeListener[A] = new WeakChangeListener[A](self)
    }

    implicit class RichListChangeListener[A](self: ListChangeListener[A]) {
      def weak: ListChangeListener[A] = new WeakListChangeListener[A](self)
    }

    implicit class RichMapChangeListener[A, B](self: MapChangeListener[A, B]) {
      def weak: MapChangeListener[A, B] = new WeakMapChangeListener[A, B](self)
    }

    implicit class RichSetChangeListener[A](self: SetChangeListener[A]) {
      def weak: SetChangeListener[A] = new WeakSetChangeListener[A](self)
    }
  }
}
