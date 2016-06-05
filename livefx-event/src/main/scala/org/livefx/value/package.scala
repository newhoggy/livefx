package org.livefx

import scala.util.DynamicVariable
import scalaz._

package object value {
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

    println(s"($source:$line:$column) => $snippet")
    new Binding[Int] {
      val subscription = liveValue.spoils.subscribe { spoilEvent =>
        bindTraceEntries.withValue(newBindTraceEntry :: bindTraceEntries.value) {
          spoil(spoilEvent.copy())
        }
      }
  
      protected def computeValue: Int = liveValue.value
    }
  }
  
  def spoilStack = bindTraceEntries.value

  def traceSpoils[V](liveValue: Live[V]): Live[V] = {
    new Binding[V] {
      protected def computeValue: V = liveValue.value
    }
  }

  def const[A](value: A): Const[A] = Const(value)

  implicit object LiveMonad extends Monad[Live] {
    override def point[A](a: => A): Live[A] = const(a)
//    override def ap[A, B](liveA: Live[A])(f: A => B): Live[B] = liveA.map(f)
//    def ap[B](f: => OptionT[F, A => B])(implicit F: Apply[F]): OptionT[F, B] =
//      OptionT(F.apply2(f.run, run) {
//        case (ff, aa) => optionInstance.ap(aa)(ff)
//      })
//    override def ap[A, B](fa: => Live[A])(f: => Live[A => B]): Live[B] = {
//      new Exception("XXX").printStackTrace(System.out)
//      bind(f)(f => map(fa)(f))
//    }
    override def map[A, B](liveA: Live[A])(f: A => B) = liveA map f
    override def bind[A, B](liveA: Live[A])(f: A => Live[B]): Live[B] = liveA.flatMap(f)
  }
}
