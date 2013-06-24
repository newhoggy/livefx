package org.livefx

import scala.util.DynamicVariable
import org.livefx.script.Spoil

class CallContext {
  val myCaller: StackTraceElement = getCaller(2)
  
  def caller: StackTraceElement = myCaller

  private def getCaller(frame: Int): StackTraceElement = {
    new Exception().getStackTrace.toSeq.drop(frame).head
  }

  private val spoilTrace = new DynamicVariable[List[StackTraceElement]](Nil)
  
  def traceSpoils[V](liveValue: Live[V]): Live[V] = new Binding[V] {
    val subscription = liveValue.spoils.subscribe { spoilEvent =>
      spoil(spoilEvent.copy())
    }

    protected def computeValue: V = liveValue.value
  }

  def traceSpoils[V](value: V): V = value
}

object CallContext extends CallContext {
  override def caller: StackTraceElement = getCaller(3)
}
