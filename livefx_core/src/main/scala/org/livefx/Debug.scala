package org.livefx

object Debug {
  def callers = new Exception().getStackTrace()
  
  def traceSpoil[T](liveValue: LiveValue[T]): LiveValue[T] = new LiveBinding[T] {
    val myCallers = callers
    val ref = liveValue.spoils.subscribe { (_, _) =>
      spoil
    }
    
    protected def computeValue: T = liveValue.value
  }

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
