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
    val ro = buffer.observableSeq
    ro.subscribe(new ro.Sub {
      override def notify(pub: ro.Pub, event: Message[Int] with Undoable): Unit = {
        println("--> " + event + " in " + pub.getClass())
      }
    })
    buffer.insert(0, 1)
    println("--> ro 0: " + ro)
    buffer.insertAll(0, List(1, 2, 3, 4, 5))
    println("--> ro 1: " + ro)
  }
}
