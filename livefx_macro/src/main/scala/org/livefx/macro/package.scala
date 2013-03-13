package org.livefx

import scala.reflect.macros.Context
import scala.language.experimental.macros

package object macro {
  def debug[T](value: T): T = macro debug_impl[T]
 
  def debug_impl[T](c: Context)(value: c.Expr[T]): c.Expr[T] = {
    import c.universe._
    val paramRep = show(value.tree)
    val paramRepTree = Literal(Constant(paramRep))
    val paramRepExpr = c.Expr[String](paramRepTree)
    val implicits = c.enclosingImplicits.map(_.toString)
    val enclosingMethodName = c.enclosingMethod match {
      case DefDef(_, name, _, _, _, _) => name.toString
      case _ => "<unknown-method>"
    }
    val methodLit = c.universe.showRaw(c.enclosingMethod)

    reify {
      println("Method lit = " + c.literal(methodLit).splice)
      println(paramRepExpr.splice + " = " + value.splice)
      println(c.literal(enclosingMethodName.toString).splice)
      value.splice
    }
  }
}
