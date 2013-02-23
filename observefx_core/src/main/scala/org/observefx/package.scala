package org

import scala.collection.mutable.ObservableBuffer
import scala.collection.mutable.Subscriber
import scala.collection.script.Message
import scala.collection.mutable.Publisher
import scala.collection.mutable.Undoable
import scala.collection.mutable.Buffer

package object observefx {
  type ObservableSeq[A] = Publisher[Message[A] with Undoable]
  
  implicit class RichObservableBuffer[A](val in: Buffer[A] with ObservableBuffer[A]) extends AnyVal {
    def observableSeq: Seq[A] with ObservableSeq[A] = in
  }
}
