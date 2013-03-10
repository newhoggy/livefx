package org.livefx

import org.livefx.script.Message
import org.livefx.script.Update
import org.livefx.script.Spoil

trait LiveValue[@specialized(Boolean, Int, Long, Double) A] extends Changeable[A, Update[A]] with Spoilable {
  type Pub <: LiveValue[A]
  
  def value: A
  
  def map[@specialized(Boolean, Int, Long, Double) B](f: A => B) = {
    val source = this
    new LiveBinding[B] {
      val ref = source.spoils.subscribeWeak((_, _) => spoil)
      
      protected override def computeValue: B = f(source.value)
    }
  }
  
  def flatMap[B](f: A => LiveValue[B]): LiveValue[B] = {
    println(Debug.callerWithoutSpecializationFrom(4))
    for (caller <- Debug.callers.take(10)) {
      println(caller)
    }
    println()
    val source = this
    val binding = new LiveBinding[B] {
      var nested: LiveValue[B] = f(source.value)
      val nestedSpoilHandler = { (_: Any, _: Any) => spoil }
      var ref: Any = nested.spoils.subscribeWeak(nestedSpoilHandler)
      val ref1 = source.spoils.subscribeWeak { (_, _) =>
        nested.spoils.unsubscribe(nestedSpoilHandler)
        nested = f(source.value)
        ref = nested.spoils.subscribeWeak(nestedSpoilHandler)
        spoil
      }
      
      protected override def computeValue: B = nested.value
    }
    binding
  }
}

object Debug {
  def callers = new Exception().getStackTrace()
  
  def callerWithoutSpecializationFrom(depth: Int): StackTraceElement = {
    Debug.callers.drop(depth).dropWhile(e => e.getMethodName.endsWith("$sp")).head
  }
}
