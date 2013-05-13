package org.livefx

import org.livefx.util.Memoize
import org.livefx.trees.indexed.Tree
import org.livefx.trees.indexed.Leaf
import org.livefx.script._

import scalaz.Monoid

trait LiveSeq[+A] extends Spoilable {
  type Pub <: LiveSeq[A]
  
  def value: Tree[A]
  
  def size: Int = value.size
  
  def changes: Events[Pub, Change[A]]

  final def fold[B >: A](implicit monoid: Monoid[B]): LiveValue[B] = {
    val outer = this
    new LiveBinding[B] {
      val spoilHandler = { (_: Any, spoilEvent: Spoil) => spoil(spoilEvent) }
      outer.spoils.subscribeWeak(spoilHandler)
      val foldImpl: Tree[B] => B = Memoize.apply(Tree.idOf(_: Tree[B])) { tree =>
        import scalaz.Scalaz._
        tree match {
          case Tree(Leaf, v, Leaf) => v |+| monoid.zero
          case Tree(l, v, Leaf) => foldImpl(l) |+| v |+| monoid.zero
          case Tree(Leaf, v, r) => v |+| foldImpl(r) |+| monoid.zero
          case Tree(l, v, r) => foldImpl(l) |+| v |+| foldImpl(r) |+| monoid.zero
          case Leaf => monoid.zero
        }
      }
      
      override def computeValue: B = foldImpl(outer.value)
    }
  }
  
  final def map[B](f: A => B): LiveSeq[B] = {
    val outer = this
    new LiveSeqBinding[B] {
      private var tree: Tree[B] = outer.value.map(f)

      val changeSubscription = outer.changes.subscribeWeak { (_, change) =>
        def handleChange(change: Change[A]): Unit = {
          change match {
            case Include(location, elem) => location match {
              case Start => tree = tree.insert(0, f(elem))
              case End => tree.insert(outer.size, f(elem))
              case Index(index) => tree.insert(index, f(elem))
              case _ => throw new IndexOutOfBoundsException
            }
            case Update(location, elem, oldElem) =>location match {
              case Start => tree = tree.update(0, f(elem))
              case End => tree.update(outer.size, f(elem))
              case Index(index) => tree.update(index, f(elem))
              case _ => throw new IndexOutOfBoundsException
            }
            case Remove(location, elem) => tree = location match {
              case Start => tree.delete(0)
              case End => tree.delete(outer.size - 1)
              case Index(index) => tree.delete(index)
              case _ => throw new IndexOutOfBoundsException
            }
            case Reset => tree = Leaf
            case script: Script[A] => script.foreach(handleChange(_))
          }
        }
        handleChange(change)
      }
      
      override def computeValue: Tree[B] = tree
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
