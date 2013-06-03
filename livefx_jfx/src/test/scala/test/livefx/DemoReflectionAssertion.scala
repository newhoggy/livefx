package test.livefx

import org.reflections.Reflections
import javafx.scene.Node
import scala.collection.JavaConversions._

object DemoReflectionAssertion {
  def subClassesOf[A](clazz: Class[A], packageName: String): Seq[Class[_ <: A]] = {
    new Reflections("javafx.scene").getSubTypesOf(clazz).toSeq
  }

  def subClassesOf[A](clazz: Class[A]): Seq[Class[_ <: A]] = {
    subClassesOf(clazz, clazz.getPackage().getName())
  }

  def main(args: Array[String]): Unit = {
    try {
      val m = scala.reflect.runtime.currentMirror
      for (clazz <- subClassesOf(classOf[Node])) {
        println(m.classSymbol(clazz))
      }
    } catch {
      case e: AssertionError => e.printStackTrace(System.err)
    }
  }
}
