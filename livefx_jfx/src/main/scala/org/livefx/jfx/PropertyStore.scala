package org.livefx.jfx

import scala.collection.immutable.HashMap

class PropertyStore[V] {
  var properties = HashMap.empty[Any, V]
}
