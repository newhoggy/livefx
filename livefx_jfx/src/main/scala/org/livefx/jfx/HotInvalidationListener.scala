package org.livefx.jfx

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleIntegerProperty
import scala.collection.immutable.HashMap

class HotInvalidationListener extends InvalidationListener {
  var store = new PropertyStore[Any]

  override def equals(o: Any): Boolean = o match {
    case that: HotInvalidationListener => {
      store = that.store
      true
    }
    case _ => false
  }

  override def hashCode(): Int = HotInvalidationListener.hashCode()

  override def invalidated(observable: Observable): Unit = Unit
}

object HotInvalidationListener
