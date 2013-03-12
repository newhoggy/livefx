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
    reify { println(paramRepExpr.splice + " = " + value.splice); value.splice }
  }
}
