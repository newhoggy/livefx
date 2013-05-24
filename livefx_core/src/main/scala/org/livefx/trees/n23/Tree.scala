package org.livefx.trees.n23

trait Tree[+A] {
  def depth: Int
  def size: Int
  final def insertAt[B >: A](index: Int, value: B): Tree[B] = Tree.insertAt(this, index, value)(identity, Branch2(_, _, _))
  final def updateAt[B >: A](index: Int, value: B): Tree[B] = Tree.updateAt(this, index, value)
  final def removeAt(index: Int): Tree[A] = Tree.removeAt(this, index)
  final def toList[B >: A](): List[B] = Tree.toList(this, Nil)
  final def toList[B >: A](tail: List[B]): List[B] = Tree.toList(this, tail)
}

final case object Tip extends Tree[Nothing] {
  final override def depth: Int = 0
  final override def size: Int = 0
  def apply[A]: Tree[A] = Tip
}

final case class Branch2[+A](a: Tree[A], v: A, b: Tree[A]) extends Tree[A] {
  final override val depth: Int = (a.depth max b.depth) + 1
  final override val size: Int = a.size + b.size + 1
  assert(a.depth + 1 == depth && b.depth + 1 == depth)
}

final case class Branch3[+A](a: Tree[A], v: A, b: Tree[A], w: A, c: Tree[A]) extends Tree[A] {
  final override val depth: Int = (a.depth max b.depth max c.depth) + 1
  final override val size: Int = a.size + b.size + c.size + 2
  assert(a.depth + 1 == depth && b.depth + 1 == depth && c.depth + 1 == depth)
}

case class Shrunk[A](value: Tree[A])

final object Tree {
  type Push[A] = (Tree[A], A, Tree[A]) => Tree[A]
  type Pull[A] = Tree[A] => Tree[A]
  
  def apply[A](values: A*): Tree[A] = {
    values.foldLeft(Tip[A])((tree, value) => tree.insertAt(tree.size, value))
  }

  def apply[A](values: List[A]): Tree[A] = {
    values.foldLeft(Tip[A])((tree, value) => tree.insertAt(tree.size, value))
  }
  
  private final def boundsCheck(test: Boolean): Unit = {
    if (!test) {
      throw new IndexOutOfBoundsException
    }
  }
  
//  private final def doKeep[A](k: Keep[A])(implicit keep: Keep[A]): Keep[A] = (xt => keep(k(xt)))
//  private final def doKeep[A](p: Push[A])(implicit keep: Keep[A]): Push[A] = ((xa, xv, xb) => keep(p(xa, xv, xb)))
//  private final def doPull[A](k: Keep[A])(implicit pull: Pull[A]): Keep[A] = (xt => pull(k(xt)))
//  private final def doPush[A](p: Push[A])(implicit push: Push[A]): Push[A] = ((xa, xv, xb) => push(p(xa, xv, xb)))
  
  final def apply[A]: Tree[A] = Tip[A]
  
  final def insertAt[A](tree: Tree[A], index: Int, value: A)(keep: Tree[A] => Tree[A], push: Push[A]): Tree[A] = {
    type N = Tree[A]
    tree match {
      case Tip => index match {
        case 0 => push(Tip, value, Tip)
        case _ => throw new IndexOutOfBoundsException
      }

      case Branch2(a, v, b) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index <= a.size =>
          Tree.insertAt(a, index, value)(k => keep(Branch2(k, v, b)), (pa, pv, pb) => keep(Branch3(pa, pv, pb, v, b)))
        case index => index - a.size - 1 match {
          case index if index <= b.size =>
            Tree.insertAt(b, index, value)(k => keep(Branch2(a, v, k)), (pa, pv, pb) => keep(Branch3(a, v, pa, pv, pb)))
          case _ => throw new IndexOutOfBoundsException
        }
      }
      case Branch3(a, v, b, w, c) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index <= a.size =>
          Tree.insertAt(a, index, value)(k => keep(Branch3(k, v, b, w, c)), (pa, pv, pb) => push(Branch2(pa, pv, pb), v, Branch2(b, w, c)))
        case index => index - a.size - 1 match {
          case index if index <= b.size =>
            Tree.insertAt(b, index, value)(k => keep(Branch3(a, v, k, w, c)), (pa, pv, pb) => push(Branch2(a, v, pa), pv, Branch2(pb, w, c)))
          case index => index - b.size - 1 match {
            case index if index <= c.size =>
              Tree.insertAt(c, index, value)((k: N) => keep(Branch3(a, v, b, w, k)), (pa, pv, pb) => push(Branch2(a, v, b), w, Branch2(pa, pv, pb)))
            case _ => throw new IndexOutOfBoundsException
          }
        }
      }
    }
  }

  def updateAt[A](tree: Tree[A], index: Int, value: A): Tree[A] = {
    type N = Tree[A]
    tree match {
      case Tip => throw new IndexOutOfBoundsException
      case Branch2(a, v, b) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size => Branch2(Tree.updateAt(a, index, value), v, b)
        case index if index == a.size => Branch2(a, value, b)
        case index => index - a.size - 1 match {
          case index if index < b.size => Branch2(a, v, Tree.updateAt(b, index, value))
          case _ => throw new IndexOutOfBoundsException
        }
      }
      case Branch3(a, v, b, w, c) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size => Branch3(Tree.updateAt(a, index, value), v, b, w, c)
        case index if index == a.size => Branch3(a, value, b, w, c)
        case index => index - a.size - 1 match {
          case index if index < b.size => Branch3(a, v, Tree.updateAt(b, index, value), w, c)
          case index if index == b.size => Branch3(a, v, b, value, c)
          case index => index - b.size - 1 match {
            case index if index < c.size => Branch3(a, v, b, w, Tree.updateAt(c, index, value))
            case index => index - b.size - 1 match {
              case index if index < c.size => Tree.updateAt(c, index, value)
              case _ => throw new IndexOutOfBoundsException
            }
          }
        }
      }
    }
  }

  def removeAt[A](tree: Tree[A], index: Int): Tree[A] = {
    println(s"--> removeAt($tree, $index)")
    type N = Tree[A]
    
    def repl[T](tree: Tree[A], keep: Tree[A] => (A => T), pull: Shrunk[A] => (A => T), leaf: T): T = {
      tree match {
        case Tip => leaf
        case Branch2(a, v, b) => repl(b, k => keep(Branch2(a, v, k)), p => mrg2r(keep, pull, a, v, p), pull(Shrunk(a))(v))
        case Branch3(a, v, b, w, c) => repl(c, k => keep(Branch3(a, v, b, w, k)), p => mrg3r(keep, a, v, b, w, p), keep(Branch2(a, v, b))(w))
      }
    }
    
    def mrg2r[T](keep: Tree[A] => T, pull: Shrunk[A] => T, t: Tree[A], w: A, s: Shrunk[A]): T = t match {
      case Branch2(ta, tv, tb) => pull(Shrunk(Branch3(ta, tv, tb, w, s.value)))
      case Branch3(ta, tv, tb, tw, tc) => keep(Branch2(Branch2(ta, tv, tb), tw, Branch2(tc, w, s.value)))
    }

    def mrg3r[T](keep: Tree[A] => T, a: Tree[A], v: A, b: Tree[A], w: A, s: Shrunk[A]): T = b match {
      case Branch2(ba, bv, bb) => keep(Branch2(a, v, Branch3(ba, bv, bb, w, s.value)))
      case Branch3(ba, bv, bb, bw, bc) => keep(Branch3(a, v, Branch2(ba, bv, bb), bw, Branch2(bc, w, s.value)))
    }
    
    def search[T](tree: Tree[A], index: Int, keep: Tree[A] => T, pull: Shrunk[A] => T): T = {
      println(s"--> search($tree, .., ..)")
      val result = tree match {
        case Tip =>
          new Exception("--> TIP").printStackTrace(System.out)
          keep(Tip)
        case Branch2(a, v, b) => {
          def mrgl(s: Shrunk[A], v: A, b: Tree[A]): T = b match {
            case Branch2(ba, bv, bb) => pull(Shrunk(Branch3(s.value, v, ba, bv, bb)))
            case Branch3(ba, bv, bb, bw, bc) => keep(Branch2(Branch2(s.value, v, ba), bv, Branch2(bb, bw, bc)))
          }
          
          index match {
            case index if index < a.size =>
              println(s"--> B2 1: index: $index, a.size: ${a.size}")
              search(a, index, k => keep(Branch2(k, v, b)), p => mrgl(p, v, b))
            case index if index == a.size =>
              println(s"--> B2 2: index: $index, a.size: ${a.size}")
              repl(a, k => r => keep(Branch2(k, r, b)), p => r => mrgl(p, r, b), pull(Shrunk(a)))
            case index if index > a.size =>
              println(s"--> B2 3: index: $index, a.size: ${a.size}")
              search(b, index - a.size - 1, k => keep(Branch2(a, v, k)), p => mrg2r(keep, pull, a, v, p))
          }
        }
        case Branch3(a, v, b, w, c) => {
          def mrgl(s: Shrunk[A], v: A, b: Tree[A], w: A, c: Tree[A]): T = b match {
            case Branch3(ba, bv, bb, bw, bc) => keep(Branch3(Branch2(s.value, v, ba), bv, Branch2(bb, bw, bc), w, c))
            case Branch2(ba, bv, bb) => keep(Branch2(Branch3(s.value, v, ba, bv, bb), w, c))
          }
          
          def mrgm(a: Tree[A], v: A, s: Shrunk[A], w: A, c: Tree[A]): T = c match {
            case Branch3(ca, cv, cb, cw, cc) => keep(Branch3(a, v, Branch2(s.value, w, ca), cv, Branch2(cb, cw, cc)))
            case Branch2(ca, cv, cb) => keep(Branch2(a, v, Branch3(s.value, w, ca, cv, cb)))
          }
  
          index match {
            case index if index < a.size =>
              println(s"--> B3 1: $index, a.size: ${a.size}")
              search(a, index, k => keep(Branch3(k, v, b, w, c)), p => mrgl(p, v, b, w, c))
            case index if index == a.size =>
              println(s"--> B3 2: $index, a.size: ${a.size}")
              repl(a, k => r => keep(Branch3(k, r, b, w, c)), p => r => mrgl(p, r, b, w, c), keep(Branch2(b, w, c)))
            case index if index > a.size => index - a.size - 1 match {
              case index if index < b.size =>
                println(s"--> B3 3: $index, a.size: ${a.size}")
                search(c, index, k => keep(Branch3(a, v, k, w, c)), p => mrgm(a, v, p, w, c))
              case index if index == b.size =>
                println(s"--> B3 4: $index, a.size: ${a.size}")
                repl(b, k => r => keep(Branch3(a, v, k, r, c)), p => r => mrgm(a, v, p, r, c), keep(Branch2(a, v, b)))
              case index if index > b.size =>
                println(s"--> B3 5: $index, a.size: ${a.size}")
                search(c, index - b.size, k => keep(Branch3(a, v, b, w, k)), p => mrg3r(keep, a, v, b, w, p))
            }
          }
        }
      }
      println("--> moo")
      result
    }
    
    return search(tree, index, identity, s => s.value)
  }
  
  final def toList[A, B >: A](tree: Tree[A], tail: List[B]): List[B] = tree match {
    case Tip => tail
    case Branch2(a, v, b) => toList(a, v::toList(b, tail))
    case Branch3(a, v, b, w, c) => toList(a, v::toList(b, w::toList(c, tail)))
  }
  
  var debugging = false
  def debug[T](f: => T): T = {
    debugging = true
    try {
      f
    } finally {
      debugging = false
    }
  }
}
