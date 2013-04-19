package org.livefx.gap

import org.livefx.Debug
import org.livefx.debug._

final case class Root[A](_config: Config = Config(16), child: Tree[A] = Leaf[A]()) {
  private implicit val config = _config
  
  final def insertL(value: A): Root[A] = {
    if (child.remainingCapacity > 0) {
      Debug.trace(s"insertL($value)", this.child.pretty(true)) {
        val result = this.copy(child = child.insertL(value))
        Debug.print("root-result: " + result.child.pretty(true))
        result
      }
    } else {
      Debug.print("insertL divide")
      child.divide match {
        case Left((l, focus)) => {
          val result = this.copy(child = Branch(l::TreesNil, focus, TreesNil)).insertL(value)
          Debug.print("result: " +  result.child.pretty(true))
          result
        }
        case Right((focus, r)) => this.copy(child = Branch(TreesNil, focus, r::TreesNil)).insertL(value)
      }
    }
  } postcondition(_.size == this.size + 1)
  
  final def insertR(value: A): Root[A] = {
    if (child.remainingCapacity > 0) {
      this.copy(child = child.insertR(value))
    } else {
      child.divide match {
        case Left((l, focus)) =>
          this.copy(child = Branch(l::TreesNil, focus, TreesNil).insertR(value))
        case Right((focus, r)) =>
          assert(r.size + focus.size == this.size)
          this.copy(child = Branch(TreesNil, focus, r::TreesNil).insertR(value))
      }
    }
  } postcondition(_.size == this.size + 1)

  final def moveBy(steps: Int): Root[A] = this.copy(child = child.moveBy(steps))
  
  final def moveTo(index: Int): Root[A] = {
    if (Debug.debug) println(s"moveBy($index - $sizeL)")
    moveBy(index - sizeL)
  }

  final def itemL: A = child.itemL
  
  final def itemR: A = child.itemR
  
  final def sizeL: Int = child.sizeL

  final def sizeR: Int = child.sizeR

  final def size: Int = child.size 
  
  final def empty: Tree[A] = child.empty

  def iterator: Iterator[A] = new Iterator[A] {
    if (Debug.debug) println(Root.this)
    private var child: Tree[A] = Root.this.child.moveTo(0)
    final override def hasDefiniteSize: Boolean = true
    final override def length: Int = child.sizeR
    final override def hasNext: Boolean = child.sizeR != 0
    final override def next(): A = {
      child = child.moveBy(1)
      child.itemL
    }
  }

  def branchLoad = Buffer.branchLoad(child)
}