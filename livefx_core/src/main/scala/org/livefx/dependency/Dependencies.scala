package org.livefx.dependency

class Dependencies(protected var _children: List[Live[Int]]) extends Live[Int] {
  def children: List[Live[Int]] = this.synchronized(_children)
  
  def children_=(list: List[Live[Int]]): Unit = this.synchronized {
    _children = list
  }
  
  override def value: Int = 0

  protected def spoilSink: org.livefx.dependency.EventSink[org.livefx.script.Spoil] = ???

  override def spoils: org.livefx.dependency.Events[org.livefx.script.Spoil] = ???
}
