package org.livefx.jfx

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleIntegerProperty
import scala.collection.immutable.HashMap

class HotInvalidationListener(val name: String) extends InvalidationListener {
  var properties = HashMap.empty[Any, Any]

  override def equals(o: Any): Boolean = o match {
    case that: HotInvalidationListener => {
      println(s"$name is stealing properties from ${that.name}")
      properties = that.properties
      true
    }
    case _ => false
  }

  override def hashCode(): Int = HotInvalidationListener.hashCode()

  override def invalidated(observable: Observable): Unit = Unit
}

object HotInvalidationListener {
  def main(args: Array[String]): Unit = {
    val property = new SimpleIntegerProperty(0)
    val listener1 = new HotInvalidationListener("a")
    val listener2 = new HotInvalidationListener("b")
    property.addListener(listener1)
    listener1.properties += ("Hello" -> "World")
    property.removeListener(listener2)
    property.addListener(listener2)
    println(s"listener2.properties = ${listener2.properties}")
  }
}
