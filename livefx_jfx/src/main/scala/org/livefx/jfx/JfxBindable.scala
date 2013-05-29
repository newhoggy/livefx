package org.livefx.jfx

import org.livefx.Binding
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.InvalidationListener
import org.livefx.jfx.Beans.Implicits._
import javafx.beans.Observable
import org.livefx.script.Spoil

trait JfxBindable { self: Binding[_] =>
  def bind(that: Observable): InvalidationListener = new InvalidationListener {
    that.addListener(this.weak)
    override def invalidated(observable: Observable): Unit = self.spoil(Spoil())
  }
}
