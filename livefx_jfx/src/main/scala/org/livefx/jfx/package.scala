package org.livefx

package object jfx {
  implicit class RichLiveVector[T](self: Live[Vector[T]]) {
    def size: Live[Int] = ???
  }
}
