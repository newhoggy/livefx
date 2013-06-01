package org.livefx.app

import scala.reflect.runtime.universe._
import javafx.scene.Node

object GenerateRiches2 {
  def main(args: Array[String]): Unit = {
    val nodeTag = typeTag[Node]
    val m = runtimeMirror(classOf[Node].getClassLoader)
    val pp = m.staticPackage("javafx.scene")
    for (dec <- pp.typeSignature.declarations) {
      println("=1=> " + dec)
    }
    for (dec <- pp.typeSignature.declarations) {
      println("=2=> " + dec)
    }
  }
}