package org.livefx

import scala.reflect.macros.Context
import scala.language.experimental.macros

package object macro {
  def debug[T](value: T): T = macro debug_impl[T]
  
  def debug_impl[T](c: Context)(value: c.Expr[T]): c.Expr[T] = {
    import c.universe._
    import c.universe.Modifiers
    val bindTraceMethod =  Select(Select(Select(Ident("org"), "org.livefx"), "org.livefx.package"), newTermName("bindTrace"))
    
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
