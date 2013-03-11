package org.livefx

object Debug {
  def callers = new Exception().getStackTrace()
  
  def traceSpoil[T](liveValue: LiveValue[T]): LiveValue[T] = new LiveBinding[T] {
    val myCallers = callers
    val ref = liveValue.spoils.subscribe { (_, _) =>
      println("6 ==> " + myCallers.seq.withoutSpecializationFrom(3))
      spoil
    }
    
    protected def computeValue: T = liveValue.value
  }
}
