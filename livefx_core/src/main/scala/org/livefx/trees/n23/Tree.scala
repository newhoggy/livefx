package org.livefx.trees.n23

trait Tree[+A] {
  def count: Int
  def size: Int
  final def insertAt[B >: A](index: Int, value: B): Tree[B] = Tree.insertAt(this, index, value, identity[Tree[B]], (l: Tree[B], v: B, r: Tree[B]) => Tree2(l, v, r))
  final def updateAt[B >: A](index: Int, value: B): Tree[B] = Tree.updateAt(this, index, value)
  final def removeAt(index: Int): Tree[A] = Tree.removeAt(this, index)
  final def toList[B >: A](): List[B] = Tree.toList(this, Nil)
  final def toList[B >: A](tail: List[B]): List[B] = Tree.toList(this, tail)
}

object Tree {
  type Keep[A] = Tree[A] => Tree[A]
  type Push[A] = (Tree[A], A, Tree[A]) => Tree[A]
  
  final def insertAt[A](tree: Tree[A], index: Int, value: A, keep: Keep[A], push: Push[A]): Tree[A] = {
    if (index < 0 || index > tree.size) throw new IndexOutOfBoundsException
    tree match {
      case Tip => Tree2[A](Tip, value, Tip)
      case Tree2(l, v, r) => {
        if (index <= l.size) {
          def myKeep: Keep[A] = (k: Tree[A]) => keep(Tree2(k, v, r))
          def myPush: Push[A] = (pl: Tree[A], pv: A, pr: Tree[A]) => keep(Tree3(pl, pv, pr, v, r))
          insertAt(l, index, value, myKeep, myPush)
        } else {
          def myKeep: Keep[A] = (k: Tree[A]) => keep(Tree2(l, v, k))
          def myPush: Push[A] = (pl: Tree[A], pv: A, pr: Tree[A]) => keep(Tree3(l, v, pl, pv, pr))
          insertAt(r, index - l.size - 1, value, myKeep, myPush)
        }
      }
      case Tree3(l, v, c, w, r) => {
        if (index <= l.size) {
          def myKeep: Keep[A] = (k: Tree[A]) => keep(Tree3(k, v, c, w, r))
          def myPush: Push[A] = (pl: Tree[A], pv: A, pr: Tree[A]) => Tree2(Tree2(pl, pv, pr), v, Tree2(c, w, r))
          insertAt(l, index, value, myKeep, myPush)
        } else if (index <= l.size + 1 + c.size) {
          def myKeep: Keep[A] = (k: Tree[A]) => keep(Tree3(l, v, k, w, r))
          def myPush: Push[A] = (pl: Tree[A], pv: A, pr: Tree[A]) => Tree2(Tree2(l, v, pl), pv, Tree2(pr, w, r))
          insertAt(c, index, value, myKeep, myPush)
        } else {
          def myKeep: Keep[A] = (k: Tree[A]) => keep(Tree3(l, v, c, w, k))
          def myPush: Push[A] = (pl: Tree[A], pv: A, pr: Tree[A]) => Tree2(Tree2(l, v, c), w, Tree2(pl, pv, pr))
          insertAt(r, index, value, myKeep, myPush)
        }
      }
    }
  }

  def updateAt[A, B <: A](tree: Tree[A], index: Int, value: B): Tree[B] = ???

  def removeAt[A](tree: Tree[A], index: Int): Tree[A] = ???
  
  final def toList[A, B >: A](tree: Tree[A], tail: List[B]): List[B] = tree match {
    case Tip => tail
    case Tree2(l, v, r) => toList(l, v::toList(r, tail))
    case Tree3(l, v, c, w, r) => toList(l, v::toList(c, w::toList(r, tail)))
  }
}

object Moo {
  def main(args: Array[String]): Unit = {
    var t: Tree[String] = Tip
    println(s"--> t = $t")
    t = t.insertAt(0, "a")
    println(s"--> insertAt(0, 'a') => $t:${t.toList}")
  }
}
