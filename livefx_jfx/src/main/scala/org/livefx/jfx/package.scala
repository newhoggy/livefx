package org.livefx

import javafx.collections.ObservableList
import javafx.collections.FXCollections
import scala.collection.JavaConversions._

package object jfx {
  implicit class RichLiveIterable[T](self: Live[Iterable[T]]) {
    def asObservableList: ObservableList[T] = {
      val targetList = FXCollections.observableArrayList[T]()
      val binding = for {
        iterable <- self
      } yield targetList.setAll(iterable.seq)
      
      return FXCollections.unmodifiableObservableList(targetList)
    }
  }
}
