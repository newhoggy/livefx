package org.livefx.gap

import org.livefx.Debug
import scalaz.Scalaz.mkIdentity

case class Config(val nodeCapacity: Int)

final case class Trees[+A](trees: List[Tree[A]]) {
  def ::[B >: A](tree: Tree[B]): Trees[B] = this.copy(trees = tree::trees)
}

object Buffer {
  def branchLoad[A](tree: Tree[A]): scala.collection.immutable.Map[Int, Int] = {
    import scalaz._
    import Scalaz._
    tree match {
      case branch: Branch[A] => branch.ls.foldLeft(Map[Int, Int]())((map, b) => map |+| branchLoad(b) + (branch.ls.length + branch.rs.length -> 1))
      case leaf: Leaf[A] => Map[Int, Int]()
    }
  }
}
