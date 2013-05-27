package org.livefx.trees.n23

import org.livefx.util.MemoizeById

trait Tree[+A] {
  def depth: Int
  def size: Int
  final def insertAt[B >: A](index: Int, value: B): Tree[B] = Tree.insertAt(this, index, value)(identity, Branch1(_, _, _))
  final def updateAt[B >: A](index: Int, value: B): Tree[B] = Tree.updateAt(this, index, value)
  final def removeAt(index: Int): (A, Tree[A]) = Tree.removeAt(this, index)
  final def removeRange(index: Int, length: Int): Tree[A] = Tree.removeRange(this, index, length)
  final def append[B >: A](that: Tree[B]): Tree[B] = Tree.append(this, that)
  final def toList[B >: A](): List[B] = Tree.toList(this, Nil)
  final def toList[B >: A](tail: List[B]): List[B] = Tree.toList(this, tail)
  final def indexedDiff[B >: A](that: Tree[B]): List[(Int, Tree[B])] = Tree.indexedDiff(this, that)
  final def map[B](f: A => B): Tree[B] = Tree.map(this, f)
}

final case object Tip extends Tree[Nothing] {
  final override def depth: Int = 0
  final override def size: Int = 0
  def apply[A]: Tree[A] = Tip
}

final case class Branch1[+A](a: Tree[A], v: A, b: Tree[A]) extends Tree[A] {
  final override val depth: Int = (a.depth max b.depth) + 1
  final override val size: Int = a.size + b.size + 1
  assert(a.depth + 1 == depth && b.depth + 1 == depth)
}

final case class Branch2[+A](a: Tree[A], v: A, b: Tree[A], w: A, c: Tree[A]) extends Tree[A] {
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

      case Branch1(a, v, b) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index <= a.size =>
          Tree.insertAt(a, index, value)(k => keep(Branch1(k, v, b)), (pa, pv, pb) => keep(Branch2(pa, pv, pb, v, b)))
        case index => index - a.size - 1 match {
          case index if index <= b.size =>
            Tree.insertAt(b, index, value)(k => keep(Branch1(a, v, k)), (pa, pv, pb) => keep(Branch2(a, v, pa, pv, pb)))
          case _ => throw new IndexOutOfBoundsException
        }
      }
      case Branch2(a, v, b, w, c) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index <= a.size =>
          Tree.insertAt(a, index, value)(k => keep(Branch2(k, v, b, w, c)), (pa, pv, pb) => push(Branch1(pa, pv, pb), v, Branch1(b, w, c)))
        case index => index - a.size - 1 match {
          case index if index <= b.size =>
            Tree.insertAt(b, index, value)(k => keep(Branch2(a, v, k, w, c)), (pa, pv, pb) => push(Branch1(a, v, pa), pv, Branch1(pb, w, c)))
          case index => index - b.size - 1 match {
            case index if index <= c.size =>
              Tree.insertAt(c, index, value)((k: N) => keep(Branch2(a, v, b, w, k)), (pa, pv, pb) => push(Branch1(a, v, b), w, Branch1(pa, pv, pb)))
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
      case Branch1(a, v, b) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size => Branch1(Tree.updateAt(a, index, value), v, b)
        case index if index == a.size => Branch1(a, value, b)
        case index => index - a.size - 1 match {
          case index if index < b.size => Branch1(a, v, Tree.updateAt(b, index, value))
          case _ => throw new IndexOutOfBoundsException
        }
      }
      case Branch2(a, v, b, w, c) => index match {
        case index if index < 0 => throw new IndexOutOfBoundsException
        case index if index < a.size => Branch2(Tree.updateAt(a, index, value), v, b, w, c)
        case index if index == a.size => Branch2(a, value, b, w, c)
        case index => index - a.size - 1 match {
          case index if index < b.size => Branch2(a, v, Tree.updateAt(b, index, value), w, c)
          case index if index == b.size => Branch2(a, v, b, value, c)
          case index => index - b.size - 1 match {
            case index if index < c.size => Branch2(a, v, b, w, Tree.updateAt(c, index, value))
            case index => index - b.size - 1 match {
              case index if index < c.size => Tree.updateAt(c, index, value)
              case _ => throw new IndexOutOfBoundsException
            }
          }
        }
      }
    }
  }

  def removeAt[A](tree: Tree[A], index: Int): (A, Tree[A]) = {
    type N = Tree[A]
    
    def repl[T](tree: Tree[A], keep: Tree[A] => (A => T), pull: Shrunk[A] => (A => T), leaf: T): T = {
      tree match {
        case Tip => leaf
        case Branch1(a, v, b) => repl(b, k => keep(Branch1(a, v, k)), p => mrg2r(keep, pull, a, v, p), pull(Shrunk(a))(v))
        case Branch2(a, v, b, w, c) => repl(c, k => keep(Branch2(a, v, b, w, k)), p => mrg3r(keep, a, v, b, w, p), keep(Branch1(a, v, b))(w))
      }
    }
    
    def mrg2r[T](keep: Tree[A] => T, pull: Shrunk[A] => T, t: Tree[A], w: A, s: Shrunk[A]): T = t match {
      case Branch1(ta, tv, tb) => pull(Shrunk(Branch2(ta, tv, tb, w, s.value)))
      case Branch2(ta, tv, tb, tw, tc) => keep(Branch1(Branch1(ta, tv, tb), tw, Branch1(tc, w, s.value)))
    }

    def mrg3r[T](keep: Tree[A] => T, a: Tree[A], v: A, b: Tree[A], w: A, s: Shrunk[A]): T = b match {
      case Branch1(ba, bv, bb) => keep(Branch1(a, v, Branch2(ba, bv, bb, w, s.value)))
      case Branch2(ba, bv, bb, bw, bc) => keep(Branch2(a, v, Branch1(ba, bv, bb), bw, Branch1(bc, w, s.value)))
    }
    
    def search[T](tree: Tree[A], index: Int, keep: Tree[A] => T, pull: Shrunk[A] => T): (A, T) = moo {
      tree match {
        case Tip => throw new IndexOutOfBoundsException
        case Branch1(a, v, b) => {
          def mrgl(s: Shrunk[A], v: A, b: Tree[A]): T = b match {
            case Branch1(ba, bv, bb) => pull(Shrunk(Branch2(s.value, v, ba, bv, bb)))
            case Branch2(ba, bv, bb, bw, bc) => keep(Branch1(Branch1(s.value, v, ba), bv, Branch1(bb, bw, bc)))
          }
          
          index match {
            case index if index < a.size =>
              search(a, index, k => keep(Branch1(k, v, b)), p => mrgl(p, v, b))
            case index if index == a.size =>
              (v, repl(a, k => r => keep(Branch1(k, r, b)), p => r => mrgl(p, r, b), pull(Shrunk(a))))
            case index if index > a.size =>
              search(b, index - a.size - 1, k => keep(Branch1(a, v, k)), p => mrg2r(keep, pull, a, v, p))
          }
        }
        case Branch2(a, v, b, w, c) => {
          def mrgl(s: Shrunk[A], v: A, b: Tree[A], w: A, c: Tree[A]): T = b match {
            case Branch2(ba, bv, bb, bw, bc) => keep(Branch2(Branch1(s.value, v, ba), bv, Branch1(bb, bw, bc), w, c))
            case Branch1(ba, bv, bb) => keep(Branch1(Branch2(s.value, v, ba, bv, bb), w, c))
          }
          
          def mrgm(a: Tree[A], v: A, s: Shrunk[A], w: A, c: Tree[A]): T = c match {
            case Branch2(ca, cv, cb, cw, cc) => keep(Branch2(a, v, Branch1(s.value, w, ca), cv, Branch1(cb, cw, cc)))
            case Branch1(ca, cv, cb) => keep(Branch1(a, v, Branch2(s.value, w, ca, cv, cb)))
          }
  
          index match {
            case index if index < a.size =>
              search(a, index, k => keep(Branch2(k, v, b, w, c)), p => mrgl(p, v, b, w, c))
            case index if index == a.size =>
              (v, repl(a, k => r => keep(Branch2(k, r, b, w, c)), p => r => mrgl(p, r, b, w, c), keep(Branch1(b, w, c))))
            case index if index > a.size => index - a.size - 1 match {
              case index if index < b.size =>
                search(b, index, k => keep(Branch2(a, v, k, w, c)), p => mrgm(a, v, p, w, c))
              case index if index == b.size =>
                (w, repl(b, k => r => keep(Branch2(a, v, k, r, c)), p => r => mrgm(a, v, p, r, c), keep(Branch1(a, v, b))))
              case index if index > b.size => index - b.size - 1 match {
                case index => {
                  search(c, index, k => keep(Branch2(a, v, b, w, k)), p => mrg3r(keep, a, v, b, w, p))
                }
              }
            }
          }
        }
      }
    }
    
    return search(tree, index, identity, s => s.value)
  }
  
  final def moo[T](f: => T): T = f
  
  final def append[A](l: Tree[A], r: Tree[A]): Tree[A] = moo {
    if (l.size == 0) {
      r
    } else if (r.size == 0) {
      l
    } else if (l.depth < r.depth) {
      val (v, lz) = l.removeAt(l.size - 1)
      
      def merge(rt: Tree[A])(keep: Tree[A] => Tree[A], push: Push[A]): Tree[A] = {
        rt match {
          case Tip => ???
          case Branch1(rta, rtv, rtb) =>
            if (rta.depth > lz.depth) {
              merge(rta)(k => keep(Branch1(k, rtv, rtb)), (pl, pv, pr) => keep(Branch2(pl, pv, pr, rtv, rtb)))
            } else if (rta.depth == lz.depth) {
              keep(Branch2(lz, v, rta, rtv, rtb))
            } else {
              keep(Branch1(lz, v, Branch1(rta, rtv, rtb)))
            }
          case Branch2(rta, rtv, rtb, rtw, rtc) =>
            if (rta.depth > lz.depth) {
              merge(rta)(k => keep(Branch2(k, rtv, rtb, rtw, rtc)), (pl, pv, pr) => push(Branch1(pl, pv, pr), rtv, Branch1(rtb, rtw, rtc)))
            } else if (rta.depth == lz.depth) {
              push(Branch1(lz, v, rta), rtv, Branch1(rtb, rtw, rtc))
            } else {
              push(lz, v, Branch2(rta, rtv, rtb, rtw, rtc))
            }
        }
      }
      
      merge(r)(identity, Branch1(_, _, _))
    } else {
      val (v, rz) = r.removeAt(0)
      
      def merge(lt: Tree[A])(keep: Tree[A] => Tree[A], push: Push[A]): Tree[A] = {
        lt match {
          case Tip => ???
          case Branch1(lta, ltv, ltb) =>
            if (lta.depth > rz.depth) {
              merge(ltb)(k => keep(Branch1(lta, ltv, k)), (pl, pv, pr) => keep(Branch2(lta, ltv, pl, pv, pr)))
            } else if (lta.depth == rz.depth) {
              keep(Branch2(lta, ltv, ltb, v, rz))
            } else {
              keep(Branch1(Branch1(lta, ltv, ltb), v, rz))
            }
          case Branch2(lta, ltv, ltb, ltw, ltc) =>
            if (lta.depth > rz.depth) {
              merge(ltc)(k => keep(Branch2(lta, ltv, ltb, ltw, k)), (pl, pv, pr) => push(Branch1(lta, ltv, ltb), ltw, Branch1(pl, pv, pr)))
            } else if (lta.depth == rz.depth) {
              push(Branch1(lta, ltv, ltb), ltw, Branch1(ltc, v, rz))
            } else {
              push(Branch2(lta, ltv, ltb, ltw, ltc), v, rz)
            }
        }
      }
      
      merge(l)(identity, Branch1(_, _, _))
    }
  }
  
  final def toList[A, B >: A](tree: Tree[A], tail: List[B]): List[B] = tree match {
    case Tip => tail
    case Branch1(a, v, b) => toList(a, v::toList(b, tail))
    case Branch2(a, v, b, w, c) => toList(a, v::toList(b, w::toList(c, tail)))
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

  final def map[A, B](self: Tree[A], f: A => B): Tree[B] = {
    self match {
      case Tip => Tip
      case Branch1(a, v, b) => Branch1(a map f, f(v), b map f)
      case Branch2(a, v, b, w, c) => Branch2(a map f, f(v), b map f, f(w), c map f)
    }
  }
  
  final def memoizedMap[A, B](f: A => B): Tree[A] => Tree[B] = {
    lazy val memoizedMap: Tree[A] => Tree[B] = MemoizeById { tree: Tree[A] =>
      tree match {
        case Tip => Tip
        case Branch1(a, v, b) => Branch1(memoizedMap(a), f(v), memoizedMap(b))
        case Branch2(a, v, b, w, c) => Branch2(memoizedMap(a), f(v), memoizedMap(b), f(w), memoizedMap(c))
      }
    }
    
    memoizedMap
  }
  
  final def removeRange[A](self: Tree[A], index: Int, length: Int): Tree[A] = {
    // TODO: Optimise for case where remove range results in very small trees.  One possible
    // strategy is to take the sum of remaining subtrees.
    (0 until length).foldLeft(self)((t, _) => t.removeAt(index)._2)
  }
  
  final def indexedDiff[A](self: Tree[A], that: Tree[A]): List[(Int, Tree[A])] = {
    // TODO: Implement indexedDiff with test property
    ???
  }
}
