package org.livefx

import org.livefx.util.Memoize
import org.livefx.trees.indexed.Tree
import org.livefx.trees.indexed.Leaf
import org.livefx.script._

import scalaz.Monoid

trait LiveSeq[+A] extends Spoilable {
  type Pub <: LiveSeq[A]
  
  def value: Tree[A]
  
  def changes: Events[Pub, Change[A]]
  
  lazy val size: LiveValue[Int] = for (tree <- asLiveValue) yield tree.size
  
  def asLiveValue: LiveValue[Tree[A]]
  
  def asLiveSeq: LiveSeq[A] = this

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
  
//  final def flatMap[B](f: A => LiveSeq[B]): LiveSeq[B] = {
//    val outer = this
//    
//    val monoid = new Monoid[LiveValue[Int]] {
//      override def zero: LiveValue[Int] = const(0)
//      override def append(liveA: LiveValue[Int], liveB: => LiveValue[Int]): LiveValue[Int] = for (a <- liveA; b <- liveB) yield a + b
//    }
//    
//    val seqs = map(f)
//    val volumes = seqs.map(_.size)
//
//    new LiveSeqBinding[B] {
//      private var tree: Tree[B] = outer.value.map(f)
//      
//      val changeSubscriber = { (_: Any, change: Change[A]) =>
//        def handleChange(change: Change[A]): Unit = {
//          change match {
//            case Include(location, elem) => {
//              val newElem = f(elem)
//              location match {
//                case Start => {
//                  tree = tree.insert(0, newElem)
//                  spoil(Spoil())
//                }
//                case End => {
//                  tree = tree.insert(outer.value.size, newElem)
//                  spoil(Spoil())
//                }
//                case Index(index) => {
//                  tree = tree.insert(index, newElem)
//                  spoil(Spoil())
//                }
//                case _ => throw new IndexOutOfBoundsException
//              }
//            }
//            case Update(location, elem, oldElem) => {
//              val newB = f(elem)
//              location match {
//                case Start => {
//                  tree = tree.update(0, newB)
//                  spoil(Spoil())
//                }
//                case End => {
//                  tree = tree.update(outer.value.size, newB)
//                  spoil(Spoil())
//                }
//                case Index(index) => {
//                  tree = tree.update(index, newB)
//                  spoil(Spoil())
//                }
//                case _ => throw new IndexOutOfBoundsException
//              }
//            }
//            case Remove(location, elem) => location match {
//              case Start => {
//                tree = tree.delete(0)
//                spoil(Spoil())
//              }
//              case End => {
//                tree = tree.delete(outer.value.size - 1)
//                spoil(Spoil())
//              }
//              case Index(index) => {
//                tree = tree.delete(index)
//                spoil(Spoil())
//              }
//              case _ => throw new IndexOutOfBoundsException
//            }
//            case Reset => tree = Leaf
//            case script: Script[A] => script.foreach(handleChange(_))
//          }
//        }
//        handleChange(change)
//      }
//
//      outer.changes.subscribeWeak(changeSubscriber) 
//      
//      override def computeValue: Tree[B] = tree
//    }
//  }
  
  
  final def map[B](f: A => B): LiveSeq[B] = {
    val outer = this
    new LiveSeqBinding[B] {
      private var tree: Tree[B] = outer.value.map(f)
      
      val changeSubscriber = { (_: Any, change: Change[A]) =>
        def handleChange(change: Change[A]): Unit = {
          change match {
            case Include(location, elem) => {
              val newElem = f(elem)
              location match {
                case Start => {
                  tree = tree.insert(0, newElem)
                  spoil(Spoil())
                }
                case End => {
                  tree = tree.insert(outer.value.size, newElem)
                  spoil(Spoil())
                }
                case Index(index) => {
                  tree = tree.insert(index, newElem)
                  spoil(Spoil())
                }
                case _ => throw new IndexOutOfBoundsException
              }
            }
            case Update(location, elem, oldElem) => {
              val newB = f(elem)
              location match {
                case Start => {
                  tree = tree.update(0, newB)
                  spoil(Spoil())
                }
                case End => {
                  tree = tree.update(outer.value.size, newB)
                  spoil(Spoil())
                }
                case Index(index) => {
                  tree = tree.update(index, newB)
                  spoil(Spoil())
                }
                case _ => throw new IndexOutOfBoundsException
              }
            }
            case Remove(location, elem) => location match {
              case Start => {
                tree = tree.delete(0)
                spoil(Spoil())
              }
              case End => {
                tree = tree.delete(outer.value.size - 1)
                spoil(Spoil())
              }
              case Index(index) => {
                tree = tree.delete(index)
                spoil(Spoil())
              }
              case _ => throw new IndexOutOfBoundsException
            }
            case Reset => tree = Leaf
            case script: Script[A] => script.foreach(handleChange(_))
          }
        }
        handleChange(change)
      }

      outer.changes.subscribeWeak(changeSubscriber) 
      
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
