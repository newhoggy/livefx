package org.livefx.app

import scala.reflect.runtime.universe._
import javafx.scene.Node
import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import java.io.PrintStream
import java.io.OutputStream
import scala.util.DynamicVariable
import javafx.scene.control.ButtonBase
import javafx.scene.control.Button

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
    for (dec <- typeTag[ButtonBase].tpe.declarations) {
      if (dec.isMethod) {
        val method = dec.asMethod
        val returnType = method.returnType
        if (Debug.suppressStdOut(returnType <:< typeOf[ObjectProperty[_]])) {
          returnType match {
            case TypeRef(_, _, handlerType :: Nil) => {
              println(s"--> ${method.name}: ${handlerType}")
              handlerType match {
                case ExistentialType(q::Nil, u) => {
                  q.typeSignature match {
                    case TypeBounds(lower, upper) => {
                      println(s"--> ${method.name} $lower, $upper")
                    }
                  }
                }
                case TypeRef(_, _, eventType :: Nil) => println(s"--> ${method.name} $eventType")
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
