package org.livefx

import org.livefx.script.Change

trait Changeable[+A, +M <: Change[A]] extends Publisher {
  type Pub <: Changeable[A, M]
  type ChangeableMessage = M
  
  def changes: Events[Pub, M]
}
