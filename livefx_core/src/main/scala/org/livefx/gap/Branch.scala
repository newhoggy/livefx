package org.livefx.gap

import org.livefx.Debug
import org.livefx.debug._

trait Branch[+A] extends Tree[A] {
  override def insert[B >: A](index: Int, value: B): Branch[B]
  override def takeCount(count: Int): Branch[A]
  override def dropCount(count: Int): Branch[A]
}

final object Branch0 extends Branch[Nothing] {
  final override val size: Int = 0
  final override def count: Int = 0

  final override def takeCount(count: Int): Branch[Nothing] = if (count == 0) Branch0 else throw new IndexOutOfBoundsException
  final override def dropCount(count: Int): Branch[Nothing] = if (count == 0) Branch0 else throw new IndexOutOfBoundsException

  final override def insert[B](index: Int, value: B): Branch[B] = throw new IndexOutOfBoundsException
}

final case class Branch1[+A](a: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size
  final override def count: Int = 1

  final override def takeCount(count: Int): Branch[A] = count match {
    case 0 => Branch0
    case 1 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Branch[A] = count match {
    case 0 => this
    case 1 => Branch0
    case _ => throw new IndexOutOfBoundsException
  }
  
  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i <= a.size) {
      return a.insert(index, value) match {
        case Branch4(a, b, c, d) => Branch2(Branch2(a, b), Branch2(c, d))
        case newA => Branch1(newA)
      }
    }

    throw new IndexOutOfBoundsException
  }
}

final case class Branch2[+A](a: Tree[A], b: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size + b.size
  final override def count: Int = 2

  final override def takeCount(count: Int): Branch[A] = count match {
    case 0 => Branch0
    case 1 => Branch1(a)
    case 2 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Branch[A] = count match {
    case 0 => this
    case 1 => Branch1(b)
    case 2 => Branch0
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i <= a.size) {
      return a.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => Branch3(Branch2(ca, cb), Branch2(cc, cd), b)
        case na => Branch2(na, b)
      }
    }

    i -= a.size

    if (i <= b.size) {
      return b.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => Branch3(a, Branch2(ca, cb), Branch2(cc, cd))
        case nb => Branch2(a, nb)
      }
    }

    throw new IndexOutOfBoundsException
  }
}

final case class Branch3[+A](a: Tree[A], b: Tree[A], c: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size + b.size + c.size
  final override def count: Int = 3

  final override def takeCount(count: Int): Branch[A] = count match {
    case 0 => Branch0
    case 1 => Branch1(a)
    case 2 => Branch2(a, b)
    case 3 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Branch[A] = count match {
    case 0 => this
    case 1 => Branch2(b, c)
    case 2 => Branch1(c)
    case 3 => Branch0
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i <= a.size) {
      return a.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => Branch4(Branch2(ca, cb), Branch2(cc, cd), b, c)
        case na => Branch3(na, b, c)
      }
    }

    i -= a.size

    if (i <= b.size) {
      return b.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => Branch4(a, Branch2(ca, cb), Branch2(cc, cd), c)
        case nb => Branch3(a, nb, c)
      }
    }

    i -= b.size

    if (i <= c.size) {
      return c.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => Branch4(a, b, Branch2(ca, cb), Branch2(cc, cd))
        case nc => Branch3(a, b, nc)
      }
    }

    throw new IndexOutOfBoundsException
  }
}

final case class Branch4[+A](a: Tree[A], b: Tree[A], c: Tree[A], d: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size + b.size + c.size + d.size
  final override def count: Int = 4

  final override def takeCount(count: Int): Branch[A] = count match {
    case 0 => Branch0
    case 1 => Branch1(a)
    case 2 => Branch2(a, b)
    case 3 => Branch3(a, b, c)
    case 4 => this
    case _ => throw new IndexOutOfBoundsException
  }

  final override def dropCount(count: Int): Branch[A] = count match {
    case 0 => this
    case 1 => Branch3(b, c, d)
    case 2 => Branch2(c, d)
    case 3 => Branch1(d)
    case 4 => Branch0
    case _ => throw new IndexOutOfBoundsException
  }

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i <= a.size) {
      return a.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => BranchN(List(Branch2(ca, cb), Branch2(cc, cd), b, c, d), 5)
        case na => Branch3(na, b, c)
      }
    }

    i -= a.size

    if (i <= b.size) {
      return b.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => BranchN(List(a, Branch2(ca, cb), Branch2(cc, cd), c, d), 5)
        case nb => Branch3(a, nb, c)
      }
    }

    i -= b.size

    if (i <= c.size) {
      return c.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => BranchN(List(a, b, Branch2(ca, cb), Branch2(cc, cd), d), 5)
        case nc => Branch3(a, b, nc)
      }
    }

    i -= c.size

    if (i <= d.size) {
      return d.insert(index, value) match {
        case Branch4(ca, cb, cc, cd) => BranchN(List(a, b, Branch2(ca, cb), Branch2(cc, cd), d), 5)
        case nc => Branch3(a, b, nc)
      }
    }

    throw new IndexOutOfBoundsException
  }
}

final case class BranchN[+A](ts: List[Tree[A]], count: Int) extends Branch[A] {
  assert(ts.size == count)

  final override val size: Int = ts.foldRight(0)(_.size + _)

  final override def takeCount(count: Int): Branch[A] = Branch(ts.take(count), count)

  final override def dropCount(count: Int): Branch[A] = Branch(ts.drop(count), this.count - count)

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    val nts = ts.flatMap { t =>
      val result = if (i <= t.size) {
        t.insert(index, value) match {
          case Branch4(ca, cb, cc, cd) => List(Branch2(ca, cb), Branch2(cc, cd))
          case na => List(na)
        }
      } else {
        Nil
      }
      
      i -= t.size
      result
    }
    
    if (i >= 0) {
      Branch(nts)
    } else {
      throw new IndexOutOfBoundsException
    }
  }
}

final object Branch {
  def apply[A](ts: List[Tree[A]], count: Int): Branch[A] = ts match {
    case List() => Branch0
    case List(a) => Branch1(a)
    case List(a, b) => Branch2(a, b)
    case List(a, b, c) => Branch3(a, b, c)
    case List(a, b, c, d) => Branch4(a, b, c, d)
    case ts => BranchN(ts, count)
  }

  def apply[A](ts: List[Tree[A]]): Branch[A] = ts match {
    case List() => Branch0
    case List(a) => Branch1(a)
    case List(a, b) => Branch2(a, b)
    case List(a, b, c) => Branch3(a, b, c)
    case List(a, b, c, d) => Branch4(a, b, c, d)
    case ts => BranchN(ts, ts.size)
  }
}
