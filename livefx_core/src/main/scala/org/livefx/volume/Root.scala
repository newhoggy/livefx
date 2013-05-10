package org.livefx.volume

import scalaz._
import Scalaz._

final case class Root[+A <% M, M: Monoid](child: Tree[A, M]) {
  final def size: Int = child.size
  final def count: Int = 0
  final def volume: M = child.volume
  final def toList[B >: A](acc: List[B]): List[B] = child.toList(acc)

  final def insert[B >: A <% M](index: Int, value: B): Root[B, M] = Root(child.insert(index, value) match {
    case Branch4(a, b, c, d) => Branch2(Branch2(a, b), Branch2(c, d))
    case Leaf4(a, b, c, d) => Branch2(Leaf2(a, b), Leaf2(c, d))
    case newChild => newChild
  })
  
  final def volumeIndex(volume: M)(implicit ordering: scala.math.Ordering[M]): Int = {
    def volumeIndex(tree: Tree[A, M], leftVolume: M): Int = {
      import Numeric.Implicits._
      import ordering._
      
      tree match {
        case Leaf0() => {
          if (leftVolume == volume) 0
          else throw new IndexOutOfBoundsException
        }
        case Leaf1(a) => {
          lazy val volume2a = leftVolume
          lazy val volume2b = volume2a |+| (a: M)
          if (volume2a < volume) throw new IndexOutOfBoundsException
          else if (volume2b < volume) 0
          else throw new IndexOutOfBoundsException
        }
        case Leaf2(a, b) => {
          lazy val volume2a = leftVolume
          lazy val volume2b = volume2a |+| (a: M)
          lazy val volume2c = volume2b |+| (b: M)
          if (volume2a < volume) throw new IndexOutOfBoundsException
          else if (volume2b < volume) 0
          else if (volume2c < volume) 1
          else throw new IndexOutOfBoundsException
        }
        case Leaf3(a, b, c) => {
          lazy val volume2a = leftVolume
          lazy val volume2b = volume2a |+| (a: M)
          lazy val volume2c = volume2b |+| (b: M)
          lazy val volume2d = volume2c |+| (c: M)
          if (volume2a < volume) throw new IndexOutOfBoundsException
          else if (volume2b < volume) 0
          else if (volume2c < volume) 1
          else if (volume2d < volume) 2
          else throw new IndexOutOfBoundsException
        }
        case Leaf4(a, b, c, d) => {
          lazy val volume2a = leftVolume
          lazy val volume2b = volume2a |+| (a: M)
          lazy val volume2c = volume2b |+| (b: M)
          lazy val volume2d = volume2c |+| (c: M)
          lazy val volume2e = volume2d |+| (d: M)
          if (volume2a < volume) throw new IndexOutOfBoundsException
          else if (volume2b < volume) 0
          else if (volume2c < volume) 1
          else if (volume2d < volume) 2
          else if (volume2e < volume) 3
          else throw new IndexOutOfBoundsException
        }
        case Branch1(a) => {
          lazy val volume2a = leftVolume
          lazy val volume2b = volume2a |+| a.volume
          if (volume2a < volume) throw new IndexOutOfBoundsException
          else if (volume2b < volume) volumeIndex(a, volume2a) 
          else throw new IndexOutOfBoundsException
        }
        case Branch2(a, b) => {
          lazy val volume2a = leftVolume
          lazy val volume2b = volume2a |+| a.volume
          lazy val volume2c = volume2b |+| b.volume
          if (volume2a < volume) throw new IndexOutOfBoundsException
          else if (volume2b < volume) volumeIndex(a, volume2a)
          else if (volume2c < volume) volumeIndex(b, volume2b)
          else throw new IndexOutOfBoundsException
        }
        case Branch3(a, b, c) => {
          lazy val volume2a = leftVolume
          lazy val volume2b = volume2a |+| a.volume
          lazy val volume2c = volume2b |+| b.volume
          lazy val volume2d = volume2c |+| c.volume
          if (volume2a < volume) throw new IndexOutOfBoundsException
          else if (volume2b < volume) volumeIndex(a, volume2a)
          else if (volume2c < volume) volumeIndex(b, volume2b)
          else if (volume2d < volume) volumeIndex(c, volume2c)
          else throw new IndexOutOfBoundsException
        }
        case Branch4(a, b, c, d) => {
          lazy val volume2a = leftVolume
          lazy val volume2b = volume2a |+| a.volume
          lazy val volume2c = volume2b |+| b.volume
          lazy val volume2d = volume2c |+| c.volume
          lazy val volume2e = volume2d |+| d.volume
          if (volume2a < volume) throw new IndexOutOfBoundsException
          else if (volume2b < volume) volumeIndex(a, volume2a)
          else if (volume2c < volume) volumeIndex(b, volume2b)
          else if (volume2d < volume) volumeIndex(c, volume2c)
          else if (volume2e < volume) volumeIndex(d, volume2d)
          else throw new IndexOutOfBoundsException
        }
        case _ => {
          throw new IndexOutOfBoundsException
        }
      }
    }

    return volumeIndex(child, volume)
  }

  final def update[B >: A <% M](index: Int, value: B): Root[B, M] = Root(child.update(index, value))

  final def remove(index: Int): (A, Root[A, M]) = child.remove(index) match { case (value, nc) => (value, Root(nc)) }
}

object Root {
  def apply[A <% M, M: Monoid](): Root[A, M] = Root[A, M](Leaf0[M]())
}
