package org.livefx

import org.livefx.script.Change
import org.livefx.script.Update
import org.livefx.util.Memoize
import org.livefx.script.Spoil
import scalaz.Monoid
import org.livefx.trees.indexed.Tree
import org.livefx.trees.indexed.Leaf

trait LiveSeq[A] extends LiveValue[Tree[A]] {
  type Pub <: LiveValue[Tree[A]]
  
  def value: Tree[A]
  
  def changes: Events[Pub, Change[A]]

  final def fold(implicit monoid: Monoid[A]): LiveValue[A] = {
    val outer = this
    new LiveBinding[A] {
      val spoilHandler = { (_: Any, spoilEvent: Spoil) => spoil(spoilEvent) }
      outer.spoils.subscribeWeak(spoilHandler)
      val foldImpl: Tree[A] => A = Memoize.apply(Tree.idOf(_: Tree[A])) { tree =>
        import scalaz.Scalaz._
        tree match {
          case Tree(Leaf, v, Leaf) => v |+| monoid.zero
          case Tree(l, v, Leaf) => foldImpl(l) |+| v |+| monoid.zero
          case Tree(Leaf, v, r) => v |+| foldImpl(r) |+| monoid.zero
          case Tree(l, v, r) => foldImpl(l) |+| v |+| foldImpl(r) |+| monoid.zero
          case Leaf => monoid.zero
        }
      }
      
      override def computeValue: A = foldImpl(outer.value)
    }
  }
  
  final def map[B](f: A => B): LiveSeq[B] = {
    val outer = this
    new LiveSeqBinding[B] {
      val spoilHandler = { (_: Any, spoilEvent: Spoil) => spoil(spoilEvent) }
      outer.spoils.subscribeWeak(spoilHandler)
      val mapImpl: Tree[A] => Tree[B] = Memoize.apply(Tree.idOf(_: Tree[A])) { tree =>
        tree match {
          case t@Tree(Leaf, v, Leaf) => t.color(Leaf, f(v), Leaf)
          case t@Tree(l, v, Leaf) => t.color(mapImpl(l), f(v), Leaf)
          case t@Tree(Leaf, v, r) => t.color(Leaf, f(v), mapImpl(r))
          case t@Tree(l, v, r) => t.color(mapImpl(l), f(v), mapImpl(r))
          case Leaf => Leaf
        }
      }
      
      override def computeValue: Tree[B] = mapImpl(outer.value)
    }
  }
  
//  def liveCounted: Map[A, Int] with LiveMap[A, Int] = {
//    val outer = this
//    new HashMap[A, Int] with LiveMap[A, Int] {
//      private final def target = this
//      def attach(key: A): Unit = {
//        val newCount = target.getOrElse(key, 0) + 1
//        target(key) = newCount 
//      }
//      def release(key: A): Unit = {
//        val oldCount = target.getOrElse(key, 0)
//        if (oldCount <= 1) {
//          target -= key
//        } else {
//          target += (key -> (oldCount - 1))
//        }
//      }
//      val result2 = outer.changes.subscribeWeak { (pub: OldLiveSeq[A], change: Change[A]) =>
//        def process(pub: OldLiveSeq[A], change: Change[A]): Unit = {
//          change match {
//            case Remove(_, oldElem) => release(oldElem)
//            case Include(_, newElem) => attach(newElem)
//            case Update(location, newElem, oldElem) => release(oldElem); attach(newElem)
//            case Reset => target.clear()
//            case s: Script[A] => for (e <- s) process(pub, e)
//          }
//        }
//  
//        process(pub, change)
//      }
//    }
//  }
//  
//  def liveSet: Set[A] with LiveSet[A] = liveCounted.liveKeys
}
