package org.livefx.app

import scala.reflect.runtime.universe._
import javafx.scene.Node
import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import java.io.PrintStream
import java.io.OutputStream
import scala.util.DynamicVariable

object Debug {
  val originalOut = System.out
  val suppressed = new DynamicVariable[Boolean](false)
  val switchedOut = new PrintStream(new OutputStream() {
    override def write(b: Int): Unit = {
      if (!suppressed.value) {
        originalOut.write(b)
      }
    }
  })
  System.setOut(switchedOut)
  def suppressStdOut[A](f: => A): A = {
    suppressed.withValue(true)(f)
  }
}

object GenerateRiches {
  def main(args: Array[String]): Unit = {
    for (dec <- typeTag[Node].tpe.declarations) {
      if (dec.isMethod) {
        val returnType = dec.asMethod.returnType
        if (Debug.suppressStdOut(returnType <:< typeOf[ObjectProperty[_]])) {
          returnType match {
            case TypeRef(_, _, handlerType :: Nil) => {
              handlerType match {
                case ExistentialType(q::Nil, u) => {
                  q.typeSignature match {
                    case TypeBounds(lower, upper) => {
                      println(s"--> $lower, $upper")
                    }
                  }
                }
                case _ =>
              }
            }
            case _ =>
          }
        }
      }
    }
  }
}
