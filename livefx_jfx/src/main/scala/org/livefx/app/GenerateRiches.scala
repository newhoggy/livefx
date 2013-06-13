package org.livefx.app

import scala.Option.option2Iterable
import scala.collection.JavaConversions.asScalaSet
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.ExistentialType
import scala.reflect.runtime.universe.ThisType
import scala.reflect.runtime.universe.TypeBounds
import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.TypeSymbol
import scala.reflect.runtime.universe.runtimeMirror
import scala.reflect.runtime.universe.typeOf
import org.livefx.util.EndLn
import org.livefx.util.IndentWriter
import org.reflections.Reflections
import javafx.beans.property.ObjectProperty
import javafx.event.Event
import javafx.scene.Node
import javafx.scene.control.TableView
import org.livefx.util.IO

object GenerateRiches {
  implicit class RichSymbol(self: reflect.runtime.universe.Symbol) {
    def asString: String = self.typeSignature match {
      case TypeBounds(lo, hi) =>
        val loName = if (!(typeOf[Nothing] =:= lo)) lo.typeSymbol.fullName + " <: " else ""
        val hiName = if (!(hi =:= typeOf[Any]))     " <: " + hi.typeSymbol.fullName else ""
        loName + self.name + hiName
    }
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
    val filteredClassSymbols = classSymbols.filter(!_.fullName.contains("$")).sortBy(_.name.toString)
    val out = new IndentWriter(System.out)
    val Regex = """on([a-zA-Z0-9_]+)Property""".r
    out.println("package org.livefx.jfx")
    out.println
    out.println("import org.livefx.Events")
    out.println
    out.println("object Nodes {")
    out.indent(2) {
      out.println("object Implicits {")
      out.indent(2) {
        var firstClass = true
        for (classSymbol <- filteredClassSymbols) {
          if (!firstClass) out.println()
          firstClass = false
          out.println(s"// classSymbol: ${classSymbol.asType}")
          val boundedParamString = classSymbol.asType.typeParams match {
            case Nil => ""
            case ps => ps.map(_.asString).mkString("[", ", ", "]")
          }
          val paramString = classSymbol.asType.typeParams match {
            case Nil => ""
            case ps => ps.map(_.name.toString).mkString("[", ", ", "]")
          }
          out.println(s"implicit class Rich${classSymbol.name}$boundedParamString(self: ${classSymbol.fullName}$paramString) {")
          out.indent(2) {
            for (classDeclaration <- classSymbol.typeSignature.declarations.toList.sortBy(_.name.toString)) {
              if (classDeclaration.isMethod) {
                val method = classDeclaration.asMethod
                val returnType = method.returnType
                if (IO.suppressStdOut(returnType <:< typeOf[ObjectProperty[_]])) {
                  returnType match {
                    case TypeRef(_, _, handlerType :: Nil) => {
                      handlerType match {
                        case ExistentialType(q::Nil, u) => {
                          q.typeSignature match {
                            case TypeBounds(lower, upper) =>
                              if (lower <:< typeOf[Event]) {
                                method.name.toString match {
                                  case Regex(name) =>
                                    if (name.length > 0) {
                                      val normalName = name(0).toString.toLowerCase + name.substring(1)
                                      out.println(s"def ${normalName}: Events[$lower] = {")
                                      out.indent(2) {
                                        out.println(s"val handler = self.${method.name}().getValue()")
                                        out.println("if (handler == null) {")
                                        out.indent(2) {
                                          out.println(s"val events = new EventsWithEventHandler[$lower]")
                                          out.println(s"self.${method.name}().setValue(events)")
                                          out.println("events")
                                        }
                                        out.println("} else {")
                                        out.indent(2) {
                                          out.println("if (handler.isInstanceOf[EventsWithEventHandler[_]]) {")
                                          out.indent(2) {
                                            out.println(s"handler.asInstanceOf[EventsWithEventHandler[$lower]]")
                                          }
                                          out.println("} else {")
                                          out.indent(2) {
                                            out.println("throw new UnsupportedOperationException(\"event handler already installed\")")
                                          }
                                          out.println("}")
                                        }
                                        out.println("}")
                                      }
                                      out.println("}")
                                    }
                                  case _ =>
                                  out.println(s"// skipped 0 ${method.name}")
                                }
                              } else {
                                out.println(s"// skipped 1 ${method.name}")
                              }
                            case _ => 
                              out.println(s"// skipped 2 ${method.name}")
                          }
                        }
                        case TypeRef(_, _, eventType :: Nil) =>
                          if (eventType <:< typeOf[Event]) {
                            method.name.toString match {
                              case Regex(name) =>
                                if (name.length > 0) {
                                  val normalName = name(0).toString.toLowerCase + name.substring(1)
                                  eventType match {
                                    case TypeRef(pre, sym, handlerType :: Nil) =>
                                      out.println(s"// handlerType: $pre: ${pre.getClass}, $sym: ${sym.getClass}, $handlerType: ${handlerType.getClass}")
                                      out.println(s"// a.normalize: ${pre.typeSymbol.fullName}")
                                      pre match {
                                        case ThisType(utt) => out.println(s"// utt: ${utt}")
                                      }
                                      out.println(s"// eventType.class: ${eventType.getClass()}")
                                      out.println(s"def ${normalName}: Events[${pre.typeSymbol.fullName}.${sym.name}[$handlerType]] = EventsWithEventHandler.on(self.${method.name}())")
                                    case _ =>
                                      out.println(s"// not a typeRef")
                                      out.println(s"// eventType.class: ${eventType.getClass()}")
                                      out.println(s"def ${normalName}: Events[$eventType] = EventsWithEventHandler.on(self.${method.name}())")
                                  }
                                } else {
                                  out.println(s"// skipped 3 ${method.name}")
                                }
                              case _ =>
                              out.println(s"// skipped 4 ${method.name}")
                            }
                          } else {
                            out.println(s"// skipped 5 ${method.name}")
                          }
                        case _ => out.println(s"// skipped 6 ${method.name}")
                      }
                    }
                    case _ =>
                  }
                }
              }
            }
          }
          out.println("}")
        }
      }
      out.println("}")
    }
    out << "}" << EndLn
  }
}
