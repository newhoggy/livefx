package org

import scala.collection.mutable.ObservableBuffer
import scala.collection.mutable.Subscriber
import scala.collection.script.Message
import scala.collection.mutable.Publisher
import scala.collection.mutable.Undoable
import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.collection.script.Script
import scala.collection.script.Include
import scala.collection.script.Message
import scala.collection.script.Remove
import scala.collection.script.Update
import scala.collection.script.Reset
import scala.collection.script.Start
import scala.collection.script.End
import scala.collection.script.Index
import scala.collection.script.NoLo

package object observefx {
  type ObservableSeq[A] = Publisher[Message[A] with Undoable]
  
  implicit class RichObservableBuffer[A](val in: Buffer[A] with ObservableBuffer[A]) {
    def asSeq: Seq[A] with ObservableSeq[A] = in
  }
  
  implicit class RichObservableSeq[A](val in: Seq[A] with ObservableSeq[A]) {
    def liveMap[B](f: A => B): Seq[B] with ObservableSeq[B] = new ArrayBuffer[B] with ObservableBuffer[B] {
      private final def target = this
      in.subscribe(new in.Sub {
        def process(pub: in.Pub, message: Message[A]): Unit = {
          message match {
            case r: Remove[A] => r.location match {
              case Start => target.remove(0)
              case End => target.remove(target.size - 1)
              case Index(index) => target.remove(index)
              case NoLo => assert(false)
            }
            case i: Include[A] => i.location match {
              case Start => target.prepend(f(i.elem))
              case End => target.append(f(i.elem))
              case Index(index) => target.insert(index, f(i.elem))
              case NoLo => assert(false)
            }
            case u: Update[A] => u.location match {
              case Start => target(0) = f(u.elem)
              case End => target(target.size - 1) = f(u.elem)
              case Index(index) => target(index) = f(u.elem)
              case NoLo => assert(false)
            }
            case x: Reset[A] => target.clear()
            case s: Script[A] => for (e <- s) process(pub, e)
          }
        }

        override def notify(pub: in.Pub, message: Message[A] with Undoable): Unit = {
          message match {
            case s: Script[A] => for (e <- s) process(pub, e)
            case _ => process(pub, message)
          }
        }
      })
    }
  }
}
