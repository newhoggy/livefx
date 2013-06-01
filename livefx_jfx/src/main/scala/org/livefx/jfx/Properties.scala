package org.livefx.jfx

import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.collections.ObservableMap
import javafx.scene.control.TableView
import javafx.scene.control.TableColumn
import javafx.scene.control.Button

object Properties {
  object Implicits {
    implicit class RichButton(self: Button) {
    }

    implicit class RichTableView[R](node: TableView[R]) {
      def items: ObjectProperty[ObservableList[R]] = node.itemsProperty()
    }
    
    implicit class RichTableColumn[R, V](node: TableColumn[R, V]) {
//      def items: ObjectProperty[ObservableList[A]] = node.itemsProperty()
    }
    
    implicit class RichProperties(node: Node) {
      def properties: ObservableMap[Object, Object] = node.getProperties()
    }
  }
}
