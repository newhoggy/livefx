package org.livefx.app

import scala.Option.option2Iterable
import scala.collection.JavaConversions.asScalaSet
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.ExistentialType
import scala.reflect.runtime.universe.TypeBounds
import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.runtimeMirror
import scala.reflect.runtime.universe.typeOf

import org.livefx.util.EndLn
import org.livefx.util.IndentWriter
import org.reflections.Reflections

import javafx.beans.property.ObjectProperty
import javafx.event.Event
import javafx.scene.Node
import javafx.scene.control.TableView

object GenerateRiches2 {
  def part1(): Unit = {
    val m = runtimeMirror(classOf[Node].getClassLoader)
    Node.impl_CSS_STYLEABLES()
    Thread.sleep(10)
    val pp = m.staticPackage("javafx.scene")
    Thread.sleep(10)
    println("==1==> " + pp.typeSignature.declarations.toList)
    println("==2==> " + pp.typeSignature.declarations.toList)
    println("==3==> " + pp.typeSignature.declarations.toList)
  }

  def part2(): Unit = {
    val x = TableView.CONSTRAINED_RESIZE_POLICY
    val y = typeOf[TableView[_]]
    val m = runtimeMirror(classOf[TableView[_]].getClassLoader)
    Thread.sleep(10)
    val pp = m.staticModule("javafx.scene.control")
    Thread.sleep(10)
    println("==1==> " + pp.typeSignature.declarations.toList)
    println("==2==> " + pp.typeSignature.declarations.toList)
    println("==3==> " + pp.typeSignature.declarations.toList)
  }

  def subClassesOf[A](clazz: Class[A], packageName: String): List[Class[_ <: A]] = {
    new Reflections("javafx.scene").getSubTypesOf(clazz).toList
  }

  def subClassesOf[A](clazz: Class[A]): List[Class[_ <: A]] = {
    subClassesOf(clazz, clazz.getPackage().getName())
  }

  def main(args: Array[String]): Unit = {
    val m = scala.reflect.runtime.currentMirror
    val classes = classOf[Node]::subClassesOf(classOf[Node])
    val classSymbols = classes.flatMap { clazz =>
      try Some(m.classSymbol(clazz)) catch { case e: AssertionError => None }
    }
    val filteredClassSymbols = classSymbols.filter(!_.fullName.contains("$"))
    val out = new IndentWriter(System.out)
    out.println("object JavaFxImplicits {")
    out.indent(2) {
      for (classSymbol <- filteredClassSymbols) {
        out.println(s"implicit class Rich${classSymbol.name}(self: ${classSymbol.fullName}) {")
        out.indent(2) {
          for (classDeclaration <- classSymbol.typeSignature.declarations) {
            if (classDeclaration.isMethod) {
              val method = classDeclaration.asMethod
              val returnType = method.returnType
              if (Debug.suppressStdOut(returnType <:< typeOf[ObjectProperty[_]])) {
                returnType match {
                  case TypeRef(_, _, handlerType :: Nil) => {
                    handlerType match {
                      case ExistentialType(q::Nil, u) => {
                        q.typeSignature match {
                          case TypeBounds(lower, upper) =>
                            if (lower <:< typeOf[Event]) {
                              out.println(s"--> ${method.name} $lower")
                            } else {
                              out.println(s"// skipped 1 ${method.name}")
                            }
                          case _ => 
                            out.println(s"// skipped 2 ${method.name}")
                        }
                      }
                      case TypeRef(_, _, eventType :: Nil) =>
                        if (eventType <:< typeOf[Event]) {
                          out.println(s"--> ${method.name} $eventType")
                        } else {
                          out.println(s"// skipped 3 ${method.name}")
                        }
                      case _ => out.println(s"// skipped 4 ${method.name}")
                    }
                  }
                  case _ =>
                }
              }
            }
          }
        }
        out.println("}")
        out.println()
      }
    }
    out << "}" << EndLn
  }
}
