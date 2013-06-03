package org.livefx.jfx.util

import scala.reflect.runtime.universe._
import javafx.scene.Node
import org.livefx.jfx.Beans
import java.io.File
import scala.collection.mutable.ArrayBuffer

class Reflection

object Reflection {
  def main(args: Array[String]): Unit = {
//    val m = runtimeMirror(Thread.currentThread().getContextClassLoader())
//    val pp = m.staticPackage("org.livefx.jfx")
//
//    println("==1==> " + pp.typeSignature.declarations.toList)
//    println("==2==> " + pp.typeSignature.declarations.toList)
//    println("==3==> " + pp.typeSignature.declarations.toList)
    
    for (c <- packageTypes("org.livefx")) {
      println(c)
    }
  }

  def packageTypes(packageName: String): Seq[Class[_]] = {
    println(s"--> packageTypes($packageName)")
    val classLoader = Thread.currentThread().getContextClassLoader()
    assert(classLoader != null)
    val path = packageName.replace('.', '/')
    val resources = classLoader.getResources(path)
    val dirs = new ArrayBuffer[File]

    while (resources.hasMoreElements()) {
      val resource = resources.nextElement()
      dirs += new File(resource.getFile())
    }

    val classes = new ArrayBuffer[java.lang.Class[_]]

    for (directory <- dirs) {
      classes ++ findClasses(directory, packageName)
    }

    return classes
  }
  
  private def findClasses(directory: File, packageName: String): Seq[Class[_]] = {
    println(s"--> findClasses($directory, $packageName)")
    val classes = new ArrayBuffer[Class[_]]
    if (!directory.exists()) {
      println("--> empty")
      return classes
    }
    val files = directory.listFiles()
    for (file <- files) {
      if (file.isDirectory()) {
        assert(!file.getName().contains("."))
        classes ++= findClasses(file, packageName + "." + file.getName())
      } else if (file.getName().endsWith(".class")) {
        classes += Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6))
      }
    }
    return classes
  }
}
