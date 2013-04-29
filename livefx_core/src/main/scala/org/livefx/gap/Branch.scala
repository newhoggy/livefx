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
  final override def toList[B](acc: List[B]): List[B] = acc

  final override def takeCount(count: Int): Branch[Nothing] = if (count == 0) Branch0 else throw new IndexOutOfBoundsException
  final override def dropCount(count: Int): Branch[Nothing] = if (count == 0) Branch0 else throw new IndexOutOfBoundsException

  final override def insert[B](index: Int, value: B): Branch[B] = throw new IndexOutOfBoundsException

  final override def update[B](index: Int, value: B): Branch[B] = throw new IndexOutOfBoundsException
}

final case class Branch1[+A](a: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size
  final override def count: Int = 1
  final override def toList[B >: A](acc: List[B]): List[B] = a.toList(acc)

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
      return a.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => Branch2(Branch2(ca, cb), Branch2(cc, cd))
        case Leaf4(ca, cb, cc, cd) => Branch2(Leaf2(ca, cb), Leaf2(cc, cd))
        case newA => Branch1(newA)
      }
    }

    throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i < a.size) {
      return Branch1(a.update(i, value))
    }

    throw new IndexOutOfBoundsException
  }
}

final case class Branch2[+A](a: Tree[A], b: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size + b.size
  final override def count: Int = 2
  final override def toList[B >: A](acc: List[B]): List[B] = a.toList(b.toList(acc))

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
      return a.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => Branch3(Branch2(ca, cb), Branch2(cc, cd), b)
        case Leaf4(ca, cb, cc, cd) => Branch3(Leaf2(ca, cb), Leaf2(cc, cd), b)
        case na => Branch2(na, b)
      }
    }

    i -= a.size

    if (i <= b.size) {
      return b.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => Branch3(a, Branch2(ca, cb), Branch2(cc, cd))
        case Leaf4(ca, cb, cc, cd) => Branch3(a, Leaf2(ca, cb), Leaf2(cc, cd))
        case nb => Branch2(a, nb)
      }
    }

    throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    println(s"Branch2($a, $b).update($index, $value)")
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i < a.size) {
      return Branch2(a.update(i, value), b)
    }

    i -= a.size

    if (i < b.size) {
      return Branch2(a, b.update(i, value))
    }

    throw new IndexOutOfBoundsException
  }
}

final case class Branch3[+A](a: Tree[A], b: Tree[A], c: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size + b.size + c.size
  final override def count: Int = 3
  final override def toList[B >: A](acc: List[B]): List[B] = a.toList(b.toList(c.toList(acc)))

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
      return a.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => Branch4(Branch2(ca, cb), Branch2(cc, cd), b, c)
        case Leaf4(ca, cb, cc, cd) => Branch4(Leaf2(ca, cb), Leaf2(cc, cd), b, c)
        case na => Branch3(na, b, c)
      }
    }

    i -= a.size

    if (i <= b.size) {
      return b.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => Branch4(a, Branch2(ca, cb), Branch2(cc, cd), c)
        case Leaf4(ca, cb, cc, cd) => Branch4(a, Leaf2(ca, cb), Leaf2(cc, cd), c)
        case nb => Branch3(a, nb, c)
      }
    }

    i -= b.size

    if (i <= c.size) {
      return c.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => Branch4(a, b, Branch2(ca, cb), Branch2(cc, cd))
        case Leaf4(ca, cb, cc, cd) => Branch4(a, b, Leaf2(ca, cb), Leaf2(cc, cd))
        case nc => Branch3(a, b, nc)
      }
    }

    throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i < a.size) {
      return Branch3(a.update(i, value), b, c)
    }

    i -= a.size

    if (i < b.size) {
      return Branch3(a, b.update(i, value), c)
    }

    i -= b.size

    if (i < c.size) {
      return Branch3(a, b, c.update(i, value))
    }

    throw new IndexOutOfBoundsException
  }
}

final case class Branch4[+A](a: Tree[A], b: Tree[A], c: Tree[A], d: Tree[A]) extends Branch[A] {
  final override val size: Int = a.size + b.size + c.size + d.size
  final override def count: Int = 4
  final override def toList[B >: A](acc: List[B]): List[B] = a.toList(b.toList(c.toList(d.toList(acc))))

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
      return a.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => BranchN(List(Branch2(ca, cb), Branch2(cc, cd), b, c, d), 5)
        case Leaf4(ca, cb, cc, cd) => BranchN(List(Leaf2(ca, cb), Leaf2(cc, cd), b, c, d), 5)
        case na => Branch4(na, b, c, d)
      }
    }

    i -= a.size

    if (i <= b.size) {
      return b.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => BranchN(List(a, Branch2(ca, cb), Branch2(cc, cd), c, d), 5)
        case Leaf4(ca, cb, cc, cd) => BranchN(List(a, Leaf2(ca, cb), Leaf2(cc, cd), c, d), 5)
        case nb => Branch4(a, nb, c, d)
      }
    }

    i -= b.size

    if (i <= c.size) {
      return c.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => BranchN(List(a, b, Branch2(ca, cb), Branch2(cc, cd), d), 5)
        case Leaf4(ca, cb, cc, cd) => BranchN(List(a, b, Leaf2(ca, cb), Leaf2(cc, cd), d), 5)
        case nc => Branch4(a, b, nc, d)
      }
    }

    i -= c.size

    if (i <= d.size) {
      return d.insert(i, value) match {
        case Branch4(ca, cb, cc, cd) => BranchN(List(a, b, c, Branch2(ca, cb), Branch2(cc, cd), d), 5)
        case Leaf4(ca, cb, cc, cd) => BranchN(List(a, b, c, Leaf2(ca, cb), Leaf2(cc, cd)), 5)
        case nd => Branch4(a, b, c, nd)
      }
    }

    throw new IndexOutOfBoundsException
  }

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    if (i < a.size) {
      return Branch4(a.update(i, value), b, c, d)
    }

    i -= a.size

    if (i < b.size) {
      return Branch4(a, b.update(i, value), c, d)
    }

    i -= b.size

    if (i < c.size) {
      return Branch4(a, b, c.update(i, value), d)
    }

    i -= c.size

    if (i < d.size) {
      return Branch4(a, b, c, d.update(i, value))
    }

    throw new IndexOutOfBoundsException
  }
}

final case class BranchN[+A](ts: List[Tree[A]], count: Int) extends Branch[A] {
  assert(ts.size == count)

  final override val size: Int = ts.foldRight(0)(_.size + _)

  final override def takeCount(count: Int): Branch[A] = Branch(ts.take(count), count)

  final override def dropCount(count: Int): Branch[A] = Branch(ts.drop(count), this.count - count)
  
  final override def toList[B >: A](acc: List[B]): List[B] = ts.foldRight(acc)((t, acc) => t.toList(acc))

  final override def insert[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    val nts = ts.flatMap { t =>
      val result = if (i <= t.size) {
        t.insert(i, value) match {
          case Branch4(ca, cb, cc, cd) => List(Branch2(ca, cb), Branch2(cc, cd))
          case Leaf4(ca, cb, cc, cd) => List(Leaf2(ca, cb), Leaf2(cc, cd))
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

  final override def update[B >: A](index: Int, value: B): Branch[B] = {
    if (index < 0) throw new IndexOutOfBoundsException
    
    var i = index
    
    val nts = ts.map { t =>
      val result = if (i <= t.size) {
        t.update(i, value)
      } else {
        t
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
