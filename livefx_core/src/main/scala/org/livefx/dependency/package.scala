package org.livefx

package object dependency {
  implicit class RichLiveInt(self: Live[Int]) {
    def incremented: Live[Int] = self.map(_ + 1)
  }
}