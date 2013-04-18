package org.livefx.gap

import org.livefx.Debug
import scalaz.Scalaz.mkIdentity

case class Config(val nodeCapacity: Int)

object Buffer {
  def branchLoad[A](tree: Tree[A]): scala.collection.immutable.Map[Int, Int] = {
    import scalaz._
    import Scalaz._
    tree match {
      case branch: Branch[A] => branch.ls.trees.foldLeft(Map[Int, Int]())((map, b) => map |+| branchLoad(b) + (branch.ls.trees.length + branch.rs.trees.length -> 1))
      case leaf: Leaf[A] => Map[Int, Int]()
    }
  }
}
