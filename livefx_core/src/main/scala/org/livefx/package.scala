package org

import scala.collection.{ mutable => mutable }
import org.livefx.script._

package object livefx {
  type ArrayBuffer[A] = mutable.ArrayBuffer[A]
  type Buffer[A] = mutable.Buffer[A]
  type HashMap[A, B] = mutable.HashMap[A, B]
  type HashSet[A] = mutable.HashSet[A]
  type Map[A, B] = mutable.Map[A, B]
  type Set[A] = mutable.Set[A]
  
  implicit class RichLiveBuffer[A](val self: Buffer[A] with LiveBuffer[A]) {
    def asSeq: Seq[A] with LiveSeq[A] = self
  }
  
  implicit class RichLiveValueBoolean(val self: LiveValue[Boolean]) {
    def &&(that: LiveValue[Boolean]): LiveValue[Boolean] =  for (l <- self; r <- that) yield l && r
    def ||(that: LiveValue[Boolean]): LiveValue[Boolean] =  for (l <- self; r <- that) yield l || r
    def unary_!(): LiveValue[Boolean] =  self.map(!_)
  }
  
  implicit class RichArrayOfStackTraceElement(val self: Seq[StackTraceElement]) {
    def withoutSpecializationFrom(depth: Int): StackTraceElement = {
      self.drop(depth).dropWhile(e => e.getMethodName.endsWith("$sp")).head
    }
  }

  def bindTrace[T](value: T): T = {println("A"); value}
  def bindTrace(liveValue: LiveValue[Int]) = {println("B"); liveValue}

  import scala.reflect.macros.Context
  import scala.language.experimental.macros

  def debug[T](value: T): T = macro debug_impl[T]
  
  def debug_impl[T](c: Context)(value: c.Expr[T]): c.Expr[T] = {
    import c.universe._
    import c.universe.Modifiers
    val bindTraceMethod =  Select(Select(Select(Ident("org"), "livefx"), "package"), newTermName("bindTrace"))
    
    def translate(expr: c.universe.Tree): c.universe.Tree = {
      expr match {
        case apply: Apply => Apply(bindTraceMethod, List(apply))
        case _ => expr
      }
    }
    
    val value2 = value match {
      case Expr(expr) => translate(expr)
    }

    reify {
      c.Expr[T](value2).splice
    }
  }
}
