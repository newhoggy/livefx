package org.livefx.trees.n23

import org.scalacheck.Properties
import org.scalacheck.Prop._
import TreeArbitrary.ArbitraryTree

object TreeSpecification extends Properties("Tree") {
  import scala.language.implicitConversions
  import scala.language.reflectiveCalls
  
  implicit def implThrows(x: => Any) = new { 
    def throws[T <: Throwable](c: Class[T]) = try { 
      x
      falsified 
    } catch { 
      case e if c.isInstance(e) => proved
      case _: Throwable => falsified 
    } 
  }

  property("insert at 0 prepends") = forAll { (tree: Tree[Int], value: Int) =>
    tree.insertAt(0, value).toList == value::tree.toList
  }

  property("insert at size appends") = forAll { (tree: Tree[Int], value: Int) =>
    tree.insertAt(tree.size, value).toList == tree.toList ::: List(value)
  }
  
  property("insert at 0 <= n <= size inserts") = forAll { (tree: Tree[Int], n: Int, value: Int) =>
    (n >= 0 && n <= tree.size) ==> {
      val list = tree.toList
      tree.insertAt(n, value).toList == list.take(n) ::: value :: list.drop(n)
    }
  }

  property("insert at n < 0 || n > size throws") = forAll { (tree: Tree[Int], n: Int, value: Int) =>
    (n < 0 || n > tree.size) ==> {
      tree.insertAt(n, value).throws(classOf[IndexOutOfBoundsException])
    }
  }

  //  property("hasRight produces value") = forAll(
//    (x: Zipper[Int]) =>
//      x.right.isEmpty || x.hasRight
//  )
//
//  property("right then left gets back") = forAll(
//    (x: Zipper[Int]) =>
//      x.right forall (_.left exists (_ == x))
//  )
//
//  property("left then right gets back") = forAll(
//    (x: Zipper[Int]) =>
//      x.left forall (_.right exists (_ == x))
//  )
//
//  property("toList compose fromList is identity") = forAll(
//    (x: List[Int]) =>
//      Zipper.fromList(x) forall (_.toList == x)
//  )
//
//  property("fromList compose toList compose start is identity") = forAll(
//    (x: Zipper[Int]) =>
//      Zipper.fromList(x.start.toList).exists(_ == x.start)
//  )
//
//  property(":= sets focus") = forAll(
//    (x: Zipper[Int], n: Int) =>
//      (x := n).focus == n
//  )
//
//  property("insertPushRight pushes right") = forAll(
//    (x: Zipper[Int], n: Int) =>
//      (x insertPushRight n).right forall (_.focus == x.focus)
//  )
//
//  property("insertPushRight sets focus") = forAll(
//    (x: Zipper[Int], n: Int) =>
//      (x insertPushRight n).focus == n
//  )
//
//  property("insertPushLeft pushes left") = forAll(
//    (x: Zipper[Int], n: Int) =>
//      (x insertPushLeft n).left forall (_.focus == x.focus)
//  )
//
//  property("insertPushLeft sets focus") = forAll(
//    (x: Zipper[Int], n: Int) =>
//      (x insertPushLeft n).focus == n
//  )
//
//  property("start has no left") = forAll(
//    (x: Zipper[Int]) =>
//      !x.start.hasLeft
//  )
//
//  property("end has no right") = forAll(
//    (x: Zipper[Int]) =>
//      !x.end.hasRight
//  )
//
//  property("cojoin is associative") = forAll(
//    (x: Zipper[Int]) =>
//      x.cojoin.cojoin == x.cojoin.map(_.cojoin)
//  )
//
//  property("swapRight compose swapRight is identity") = forAll(
//    (x: Zipper[Int]) =>
//      x.swapRight.swapRight == x
//  )
//
//  property("swapLeft compose swapLeft is identity") = forAll(
//    (x: Zipper[Int]) =>
//      x.swapLeft.swapLeft == x
//  )
//
//  property("deletePullRight compose insertPushRight is identity") = forAll(
//    (x: Zipper[Int], n: Int) =>
//      (x.insertPushRight(n).deletePullRight) == x
//  )
//
//  property("deletePullLeft compose insertPushLeft is identity") = forAll(
//    (x: Zipper[Int], n: Int) =>
//      (x.insertPushLeft(n).deletePullLeft) == x
//  )
//
//  property("rightN distributes addition in positive ranges") = forAll(
//    (x: Zipper[Int], m: Int, n: Int) => {
//      val mm = m.abs
//      val nn = n.abs
//      (mm > 0 && mm < 100 && nn > 0 && nn < 100) ==> ((x.rightN(mm) flatMap (_.rightN(nn))) == x.rightN(mm + nn))
//    }
//  )
//
//  property("rightN distributes addition in negative ranges") = forAll(
//    (x: Zipper[Int], m: Int, n: Int) => {
//      val mm = m.abs
//      val nn = n.abs
//      (mm < 0 && mm > -100 && nn < 0 && nn > -100) ==> ((x.rightN(mm) flatMap (_.rightN(nn))) == x.rightN(mm + nn))
//    }
//  )
//
//  property("leftN distributes addition in positive ranges") = forAll(
//    (x: Zipper[Int], m: Int, n: Int) => {
//      val mm = m.abs
//      val nn = n.abs
//      (mm > 0 && mm < 100 && nn > 0 && nn < 100) ==> ((x.leftN(mm) flatMap (_.leftN(nn))) == x.leftN(mm + nn))
//    }
//  )
//
//  property("leftN distributes addition in negative ranges") = forAll(
//    (x: Zipper[Int], m: Int, n: Int) => {
//      val mm = m.abs
//      val nn = n.abs
//      (mm < 0 && mm > -100 && nn < 0 && nn > -100) ==> ((x.leftN(mm) flatMap (_.leftN(nn))) == x.leftN(mm + nn))
//    }
//  )
//
//  property("nth(n) is same as rightN(n) compose start") = forAll(
//    (x: Zipper[Int], n: Int) => (n < 100 && n > -10) ==> {
//      (x nth n) == (x.start rightN n)
//    }
//  )
}
