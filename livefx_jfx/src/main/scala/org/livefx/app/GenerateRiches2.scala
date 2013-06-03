package org.livefx.app

import scala.reflect.runtime.universe._
import javafx.scene.Node
import javafx.scene.control.TableView
import org.reflections.Reflections
import scala.collection.JavaConversions._
import org.livefx.util.IndentWriter

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

  def subClassesOf[A](clazz: Class[A], packageName: String): Seq[Class[_ <: A]] = {
    new Reflections("javafx.scene").getSubTypesOf(clazz).toSeq
  }

  def subClassesOf[A](clazz: Class[A]): Seq[Class[_ <: A]] = {
    subClassesOf(clazz, clazz.getPackage().getName())
  }

  def main(args: Array[String]): Unit = {
    val m = scala.reflect.runtime.currentMirror
    val classes = subClassesOf(classOf[Node])
    val classSymbols = classes.flatMap { clazz =>
      try Some(m.classSymbol(clazz)) catch { case e: AssertionError => None }
    }
    val filteredClassSymbols = classSymbols.filter(!_.fullName.contains("$"))
    val out = new IndentWriter(System.out)
    for (classSymbol <- filteredClassSymbols) {
      out.indent(2) {
        out.println(classSymbol.fullName)
      }
    }
  }
}
