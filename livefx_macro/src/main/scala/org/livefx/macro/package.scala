package org.livefx

import scala.reflect.macros.Context
import scala.language.experimental.macros

package object macro {
  def debug[T](value: T): T = macro debug_impl[T]
 
  def debug_impl[T](c: Context)(value: c.Expr[T]): c.Expr[T] = {
    import c.universe._
    import c.universe.Modifiers
    import scala.reflect.internal.Trees
    val paramRep = show(value.tree)
    val paramRepTree = Literal(Constant(paramRep))
    val paramRepExpr = c.Expr[String](paramRepTree)
    val implicits = c.enclosingImplicits.map(_.toString)
    var x: scala.reflect.api.Trees#Modifiers = null
    val pos = c.enclosingMethod.pos.toString()

    val enclosingMethodName = c.enclosingMethod match {
      case DefDef(modifiers, name, _, _, _, _) =>
//        modifiers match {
//          case modifiers: {} =>
//        }
        modifiers + ":" + modifiers.annotations.toString() + ":"  + (modifiers.annotations mkString ", ")
      case _ => "<unknown-method>"
    }

    val methodLit1 = c.universe.showRaw(value)
    val methodLit2 = value match {
      case Expr(x) => x.pos.source + ":" + x.pos.line + ":" + x.pos.column
    }
    
    reify {
      implicit val x = 1
    }

    reify {
      println("Method lit = " + c.literal(methodLit1).splice)
      println("Method lit = " + c.literal(methodLit2).splice)
      println("Method lit = " + c.literal(pos).splice)
//      println(paramRepExpr.splice + " = " + value.splice)
      println("--> " + c.literal(enclosingMethodName.toString).splice)
      value.splice
    }
  }
}
