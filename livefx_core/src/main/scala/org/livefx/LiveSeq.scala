package org.livefx

import org.livefx.script.Change
import org.livefx.trees.indexed.Tree
import org.livefx.script.Update

trait LiveSeq[A] extends LiveValue[Tree[A]] {
  type Pub <: LiveValue[Tree[A]]
  
  def value: Tree[A]
  
  def changes: Events[Pub, Change[A]]
}
