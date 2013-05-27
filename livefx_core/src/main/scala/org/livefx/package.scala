package org

import scala.collection.{ mutable => mutable }
import org.livefx.script._
import scala.util.DynamicVariable
import org.livefx.trees.n23.Tree

package object livefx {
  type ArrayBuffer[A] = mutable.ArrayBuffer[A]
  type Buffer[A] = mutable.Buffer[A]
  type HashMap[A, B] = mutable.HashMap[A, B]
  type HashSet[A] = mutable.HashSet[A]
  type Map[A, B] = mutable.Map[A, B]
  type Set[A] = mutable.Set[A]

  implicit class RichLiveTreeSeq[A](self: LiveValue[Tree[A]]) {
//    def map[B](f: A => B): Tree[B] = for {
//      tree <- self
//    } yield tree.map(f)
    
    // TODO: Implement flatMap for RichLiveTreeSeq.
    def flatMap[B](f: A => Tree[B]): Tree[B] = ???
  }
  
  implicit class RichLiveBuffer[A](val self: Buffer[A] with LiveBuffer[A]) {
    def asSeq: Seq[A] with OldLiveSeq[A] = self
  }
  
  implicit class RichLiveValueBoolean(val self: LiveValue[Boolean]) {
    def &&(that: LiveValue[Boolean]): LiveValue[Boolean] =  for (l <- self; r <- that) yield l && r
    def ||(that: LiveValue[Boolean]): LiveValue[Boolean] =  for (l <- self; r <- that) yield l || r
    def unary_!(): LiveValue[Boolean] =  self.map(!_)
    
    def select[T](liveA: LiveValue[T], liveB: LiveValue[T]): LiveValue[T] = for (v <- self; ab <- if (v) liveA else liveB) yield ab
  }

  implicit class RichLiveOptionLiveValue[T](val self: LiveValue[Option[LiveValue[T]]]) {
    def orElse(that: LiveValue[T]): LiveValue[T] = for {
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

  def bindTrace(liveValue: LiveValue[Int]) = {println("B"); liveValue}

  def bindTrace(liveValue: LiveValue[Int], source: String, line: Int, column: Int, snippet: String) = {
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

  def traceSpoils[V](liveValue: LiveValue[V]): LiveValue[V] = {
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
