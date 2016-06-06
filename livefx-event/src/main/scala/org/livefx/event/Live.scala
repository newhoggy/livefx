package org.livefx.event

/** A value that may change over time.  There is also an event source that emits the new value every
  * time the value changes.
  */
trait Live[A] {
  def value: A

  def source: Source[A]
}
