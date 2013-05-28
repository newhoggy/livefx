package org

import org.livefx.script._
import scala.util.DynamicVariable
import org.livefx.trees.n23.Tree

package object livefx {
  implicit class RichLiveValueBoolean(val self: Live[Boolean]) {
    def &&(that: Live[Boolean]): Live[Boolean] =  for (l <- self; r <- that) yield l && r
    def ||(that: Live[Boolean]): Live[Boolean] =  for (l <- self; r <- that) yield l || r
    def unary_!(): Live[Boolean] =  self.map(!_)
    
    def select[T](liveA: Live[T], liveB: Live[T]): Live[T] = for (v <- self; ab <- if (v) liveA else liveB) yield ab
  }

  implicit class RichLiveOptionLiveValue[T](val self: Live[Option[Live[T]]]) {
    def orElse(that: Live[T]): Live[T] = for {
      maybeSelfValue <- self
      value <- maybeSelfValue.getOrElse(that)
    } yield value
  }

  implicit class RichArrayOfStackTraceElement(val self: Seq[StackTraceElement]) {
    def withoutSpecializationFrom(depth: Int): StackTraceElement = {
      self.drop(depth).dropWhile(e => e.getMethodName.endsWith("$sp")).head
    }
  }

  private val bindTraceEntries = new DynamicVariable[List[BindTraceEntry]](Nil)

  def bindTrace[T](value: T): T = {println("A"); value}

  def bindTrace[T](value: T, source: String, line: Int, column: Int, snippet: String): T = {println("A2"); value}

  def bindTrace(liveValue: Live[Int]) = {println("B"); liveValue}

  def bindTrace(liveValue: Live[Int], source: String, line: Int, column: Int, snippet: String) = {
    val newBindTraceEntry = BindTraceEntry(source, line, column, snippet)

    println(s"(${source}:${line}:${column}) => ${snippet}")
    new LiveBinding[Int] {
      liveValue.spoils.subscribe { (_, _) =>
        bindTraceEntries.withValue(newBindTraceEntry :: bindTraceEntries.value) {
          spoil()
        }
      }
  
      protected def computeValue: Int = liveValue.value
    }
  }
  
  def spoilStack = bindTraceEntries.value

  def traceSpoils[V](liveValue: Live[V]): Live[V] = {
    val caller = CallContext.caller

    new LiveBinding[V] {
      val ref = liveValue.spoils.subscribeWeak { (_, spoilEvent) =>
        spoil(Spoil(caller :: spoilEvent.trace))
      }
  
      protected def computeValue: V = liveValue.value
    }
  }

  def const[A](value: A): Const[A] = Const(value)
}
