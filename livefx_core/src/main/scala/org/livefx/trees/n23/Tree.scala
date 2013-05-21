package org.livefx.trees.n23

case class Tree[+A](root: Node[A] = Empty, depth: Int = 0) {
  final def size: Int = root.size
  final def insertAt[B >: A](index: Int, value: B): Tree[B] = Tree(Tree.insertAt(root, index, value)(identity, Branch2[B](_, _)))
  final def updateAt[B >: A](index: Int, value: B): Tree[B] = Tree(Tree.updateAt(root, index, value))
  final def removeAt(index: Int): Tree[A] = root match {
    case Tip(a) => Tree(Empty)
    case tree => Tree(Tree.removeAt(tree, index)(identity)(identity))
  }
  final def toList[B >: A](): List[B] = Tree.toList(root, Nil)
  final def toList[B >: A](tail: List[B]): List[B] = Tree.toList(root, tail)
}

trait Node[+A] {
  def count: Int
  def size: Int
}

final case object Empty extends Node[Nothing] {
  final override def count: Int = 0
  final override def size: Int = 0
}
  
final case class Tip[+A](a: A) extends Node[A] {
  final override def count: Int = 1
  final override def size: Int = 1
}

sealed trait Branch[+A] extends Node[A] {
  def nodeIndex(index: Int): Node[A]
}

final case class Branch2[+A](a: Node[A], b: Node[A]) extends Branch[A] {
  final override def count: Int = 2
  final override val size: Int = a.size + b.size
  final override def nodeIndex(index: Int): Node[A] = index match {
    case 0 => a
    case 1 => b
    case _ => throw new IndexOutOfBoundsException
  }
}

final case class Branch3[+A](a: Node[A], b: Node[A], c: Node[A]) extends Branch[A] {
  final override def count: Int = 3
  final override val size: Int = a.size + b.size + c.size
  final override def nodeIndex(index: Int): Node[A] = index match {
    case 0 => a
    case 1 => b
    case 2 => c
    case _ => throw new IndexOutOfBoundsException
  }
}

final case class Branch4[+A](a: Node[A], b: Node[A], c: Node[A], d: Node[A]) extends Branch[A] {
  final override def count: Int = 4
  final override val size: Int = a.size + b.size + c.size + d.size
  final override def nodeIndex(index: Int): Node[A] = index match {
    case 0 => a
    case 1 => b
    case 2 => c
    case 3 => d
    case _ => throw new IndexOutOfBoundsException
  }
}

final object Tree {
  type Push[A] = (Node[A], Node[A]) => Node[A]
  type Keep[A] = Node[A] => Node[A]
  type Pull[A] = Node[A] => Node[A]
  
  def apply[A](values: A*): Tree[A] = {
    values.foldLeft(Tree[A](Empty))((tree, value) => tree.insertAt(tree.size, value))
  }

  def apply[A](values: List[A]): Tree[A] = {
    values.foldLeft(Tree[A](Empty))((tree, value) => tree.insertAt(tree.size, value))
  }
  
  private final def boundsCheck(test: Boolean): Unit = {
    if (!test) {
      throw new IndexOutOfBoundsException
    }
  }
  
  private final def doKeep[A](k: Node[A] => Node[A]): Keep[A] = k
  private final def doPull[A](k: Node[A] => Node[A]): Keep[A] = k
  private final def doPush[A](p: (Node[A], Node[A]) => Node[A]): Push[A] = p 
  
  final def apply(): Tree[Nothing] = Tree(Empty)
  
  final def insertAt[A](tree: Node[A], index: Int, value: A)(keep: Keep[A], push: Push[A]): Node[A] = {
    type N = Node[A]
    tree match {
      case Empty => index match {
        case 0 => keep(Tip(value))
        case _ => throw new IndexOutOfBoundsException
      }
      case Tip(a) => index match {
        case 0 => push(Tip(value), Tip(a))
        case 1 => push(Tip(a), Tip(value))
        case _ => throw new IndexOutOfBoundsException
      }
      case Branch2(a, b) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size =>
          Tree.insertAt(a, index, value)((k: N) => keep(Branch2(k, b)), (p: N, q: N) => keep(Branch3(p, q, b)))
        case index => index - a.size match {
          case index if index <= b.size =>
            Tree.insertAt(b, index, value)((k: N) => keep(Branch2(a, k)), (p: N, q: N) => keep(Branch3(a, p, q)))
          case _ => throw new IndexOutOfBoundsException
        }
      }
      case Branch3(a, b, c) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size =>
          Tree.insertAt(a, index, value)((k: N) => keep(Branch3(k, b, c)), (p: N, q: N) => push(Branch2(p, q), Branch2(b, c)))
        case index => index - a.size match {
          case index if index < b.size =>
            Tree.insertAt(b, index, value)((k: N) => keep(Branch3(a, k, c)), (p: N, q: N) => push(Branch2(a, p), Branch2(q, c)))
          case index => index - b.size match {
            case index if index <= c.size =>
              Tree.insertAt(c, index, value)((k: N) => keep(Branch3(a, b, k)), (p: N, q: N) => push(Branch2(a, b), Branch2(p, q)))
            case _ => throw new IndexOutOfBoundsException
          }
        }
      }
    }
  }

  def updateAt[A](tree: Node[A], index: Int, value: A): Node[A] = {
    type N = Node[A]
    tree match {
      case Empty => throw new IndexOutOfBoundsException
      case Tip(a) => index match {
        case 0 => Tip(value)
        case _ => throw new IndexOutOfBoundsException
      }
      case Branch2(a, b) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size => Tree.updateAt(a, index, value)
        case index => index - a.size match {
          case index if index < b.size => Tree.updateAt(b, index, value)
          case _ => throw new IndexOutOfBoundsException
        }
      }
      case Branch3(a, b, c) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size => Tree.updateAt(a, index, value)
        case index => index - a.size match {
          case index if index < b.size => Tree.updateAt(b, index, value)
          case index => index - b.size match {
            case index if index < c.size => Tree.updateAt(c, index, value)
            case _ => throw new IndexOutOfBoundsException
          }
        }
      }
    }
  }

  def removeAt[A](tree: Node[A], index: Int)(keep: Keep[A])(pull: Pull[A]): Node[A] = {
    type N = Node[A]
    tree match {
      case Branch2(Tip(a), Tip(b)) => index match {
        case 0 => pull(Tip(b))
        case 1 => pull(Tip(a))
        case _ => throw new IndexOutOfBoundsException
      }
      case Branch3(Tip(a), Tip(b), Tip(c)) => index match {
        case 0 => keep(Branch2(Tip(b), Tip(c)))
        case 1 => keep(Branch2(Tip(a), Tip(c)))
        case 2 => keep(Branch2(Tip(a), Tip(b)))
        case _ => throw new IndexOutOfBoundsException
      }
      case Branch2(a: Branch[A], b: Branch[A]) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size =>
          Tree.removeAt(a, index)((k: N) => keep(Branch2(k, b))) { (p: N) =>
            b match {
              case Branch2(x, y) => pull(Branch3(p, x, y))
              case Branch3(x, y, z) => keep(Branch2(Branch2(p, x), Branch2(y, z)))
              case _ => ???
            }
          }
        case index => index - a.size match {
          case index if index <= b.size =>
            Tree.removeAt(b, index)((k: N) => keep(Branch2(a, k))) { (p: N) =>
              a match {
                case Branch2(x, y) => pull(Branch3(x, y, b))
                case Branch3(x, y, z) => keep(Branch2(Branch2(x, y), Branch2(z, b)))
                case _ => ???
              }
            }
          case _ => throw new IndexOutOfBoundsException
        }
      }
      case Branch3(a, b, c) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size =>
          Tree.removeAt(a, index)((k: N) => keep(Branch3(k, b, c))) { p =>
            b match {
              case Branch2(ba, bb) => keep(Branch2(Branch3(p, ba, bb), c))
              case Branch3(ba, bb, bc) => keep(Branch3(Branch2(p, ba), Branch2(ba, bb), c))
            }
          }
        case index => index - a.size match {
          case index if index < b.size =>
            Tree.removeAt(b, index)((k: N) => keep(Branch3(a, k, c)))(p => keep(Branch2(a, c)))
          case index => index - b.size match {
            case index if index <= c.size =>
              Tree.removeAt(c, index)((k: N) => keep(Branch3(a, b, k)))(p => keep(Branch2(a, b)))
            case _ => throw new IndexOutOfBoundsException
          }
        }
      }
    }
  }
  
  final def toList[A, B >: A](tree: Node[A], tail: List[B]): List[B] = tree match {
    case Empty => tail
    case Tip(a) => a::tail
    case Branch2(a, b) => toList(a, toList(b, tail))
    case Branch3(a, b, c) => toList(a, toList(b, toList(c, tail)))
    case Branch4(a, b, c, d) => toList(a, toList(b, toList(c, toList(d, tail))))
  }
}

object Moo {
  def main(args: Array[String]): Unit = {
    var t: Tree[String] = Tree()
    println(s"--> t = $t")
    t = t.insertAt(0, "a")
    println(s"--> insertAt(0, 'a') => $t:${t.toList}")
  }
}
