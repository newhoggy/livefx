package org

import scala.collection.{ mutable => mutable }
import org.livefx.script._

package object livefx {
  type ArrayBuffer[A] = mutable.ArrayBuffer[A]
  type Buffer[A] = mutable.Buffer[A]
  type HashMap[A, B] = mutable.HashMap[A, B]
  type HashSet[A] = mutable.HashSet[A]
  type Map[A, B] = mutable.Map[A, B]
  type Set[A] = mutable.Set[A]
  
  implicit class RichLiveBuffer[A](val self: Buffer[A] with LiveBuffer[A]) {
    def asSeq: Seq[A] with LiveSeq[A] = self
  }
  
  implicit class RichLiveValueBoolean(val self: LiveValue[Boolean]) {
    def &&(that: LiveValue[Boolean]): LiveValue[Boolean] =  for (l <- self; r <- that) yield l && r
    def ||(that: LiveValue[Boolean]): LiveValue[Boolean] =  for (l <- self; r <- that) yield l || r
    def unary_!(): LiveValue[Boolean] =  self.map(!_)
  }
}
