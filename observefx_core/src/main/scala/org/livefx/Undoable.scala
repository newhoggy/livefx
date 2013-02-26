package org.livefx

trait Undoable {
  /** Undo the last operation.
   */
  def undo(): Unit
}
