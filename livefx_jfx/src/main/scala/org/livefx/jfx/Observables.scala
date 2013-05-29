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

object Observables {
  object Implicits {
    implicit class RichProperty[A](self: Property[A]) {
      def <==(value: A): Unit = self.setValue(value)
      def value: A = self.getValue()
    }

    implicit class RichObservableValue[A](self: ObservableValue[A]) {
      
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
