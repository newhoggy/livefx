package org.livefx

object Memoizers {
  import org.livefx.util._

  object IndexedTree {
    import org.livefx.trees.indexed._
    
    def treeId[A](t: Tree[A]): Any = t.id

    val sumInt: Tree[Int] => Int = Memoize(treeId[Int]) { t: Tree[Int] =>
      t match {
        case Red(l, v, r) => sumInt(l) + v + sumInt(r)
        case Black(l, v, r) => sumInt(l) + v + sumInt(r)
        case Leaf => 0
      }
    }
  }
}
