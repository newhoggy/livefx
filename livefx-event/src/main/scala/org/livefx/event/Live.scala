package org.livefx.event

/** A value that may change over time.  This is also an event source that emits the new value every
  * time the value changes.
  */
trait Live[A] extends Source[A] {
  def value: A
}
