package org

import scala.collection.mutable.ObservableBuffer
import scala.collection.mutable.Subscriber
import scala.collection.script.Message
import scala.collection.mutable.Publisher
import scala.collection.mutable.Undoable
import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer

package object observefx {
  type ObservableSeq[A] = Publisher[Message[A] with Undoable]
  
  implicit class RichObservableBuffer[A](val in: Buffer[A] with ObservableBuffer[A]) {
    def asSeq: Seq[A] with ObservableSeq[A] = in
  }
  
  implicit class RichObservableSeq[A](val in: Seq[A] with ObservableSeq[A]) {
      def map[B](f: A => B): Seq[A] with ObservableSeq[A] = new ArrayBuffer[A] with ObservableBuffer[A] {
      in.subscribe(new in.Sub {
        override def notify(pub: in.Pub, event: Message[A] with Undoable): Unit = {
          
        }
      })
    }
  }
}
