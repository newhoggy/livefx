package org

import scala.collection.mutable.ObservableBuffer
import scala.collection.mutable.Subscriber
import scala.collection.script.Message
import scala.collection.mutable.Publisher
import scala.collection.mutable.Undoable
import scala.collection.mutable.Buffer

package object observefx {
  type ObservableSeq[A] = Publisher[Message[A]]
  
  class ObservableSeqOnBuffer[A](in: ObservableBuffer[A]) extends ObservableSeq[A] {
    def apply(idx: Int): A = in.apply(idx)
    def length: Int = in.length
    def iterator: Iterator[A] = in.iterator
    
    in.subscribe(new Subscriber[Message[A], ObservableBuffer[A]] {
      override def notify(pub: ObservableBuffer[A], event: Message[A]): Unit = {
        publish(event)
      }
    })
  }
  
  implicit class RichObservableBuffer[A](val in: Buffer[A] with ObservableBuffer[A]) extends AnyVal {
    def immutable: ObservableSeq[A] = new ObservableSeqOnBuffer(in)
    def observableSeq: Seq[A] with Publisher[Message[A] with Undoable] = in
  }
}
