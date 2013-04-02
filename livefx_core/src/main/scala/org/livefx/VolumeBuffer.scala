package org.livefx

import scala.reflect.ClassTag
import scalaz.Monoid

class VolumeZipper[A: ClassTag] {
}

trait HasVolume[A] {
  type V

  implicit def monoid: Monoid[V]

  def volumeOf(value: A): V
}

case class Volume(index: Int, volume: Int)

trait VolumeMonoid extends Monoid[Volume] {
  val zero: Volume = Volume(0, 0)
  
  def append(s1: Volume, s2: => Volume): Volume = Volume(s1.index + s2.index, s1.volume + s2.volume)
}

object VolumeMonoid extends VolumeMonoid

abstract class VolumeNode[A: ClassTag]

class VolumeLeaf[A: ClassTag]

class VolumeBuffer[A: ClassTag](implicit val hasVolume: HasVolume[A]) {
  implicit def monoid: Monoid[hasVolume.V] = hasVolume.monoid
  
  def me(): Unit = {
    val x = new VolumeLeaf[Int]
  }
}
