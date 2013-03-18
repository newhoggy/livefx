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
        case apply: Apply =>
          System.err.println("---> apply: " + c.universe.showRaw(apply))
          System.err.println("---> position: " + apply.pos.source + ", " + apply.pos.line)
          Apply(bindTraceMethod, List(
              apply,
              Literal(Constant(apply.pos.source.toString)),
              Literal(Constant(apply.pos.line)),
              Literal(Constant(apply.pos.column)),
              Literal(Constant(apply.pos.lineContent))))
        case _ =>
          System.err.println("---> expr: " + c.universe.showRaw(expr))

          expr
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
