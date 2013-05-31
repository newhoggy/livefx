package org.livefx.app

import scala.reflect.runtime.universe._
import javafx.scene.Node
import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent

object GenerateRiches {
  def main(args: Array[String]): Unit = {
    for (dec <- typeTag[Node].tpe.declarations) {
      if (dec.isMethod) {
        val returnType = dec.asMethod.returnType 
        if (returnType <:< typeOf[ObjectProperty[EventHandler[_ >: KeyEvent]]]) {
          println(dec.asMethod.name + ": " + dec.asMethod.returnType)
        }
      }
    }
  }
}
