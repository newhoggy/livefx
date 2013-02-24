package org.observefx

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ObservableBuffer
import scala.collection.mutable.Subscriber
import scala.collection.script.Message
import scala.collection.mutable.Undoable
import scala.collection.mutable.Publisher

object Main {
  def main(args: Array[String]): Unit = {
    val buffer = new ArrayBuffer[Int] with ObservableBuffer[Int]
    val stringSeq = buffer.liveMap { value => "[" + value + "]" }
    val ro = buffer.asSeq
    stringSeq.subscribe(new stringSeq.Sub {
      override def notify(pub: stringSeq.Pub, event: Message[String] with Undoable): Unit = {
        println("--> " + event + " stringSeq " + pub.getClass())
      }
    })
    buffer.insert(0, 1)
    println("--> ro 0: " + stringSeq)
    buffer.insertAll(0, List(1, 2, 3, 4, 5))
    println("--> ro 1: " + stringSeq)
    
  }
}
