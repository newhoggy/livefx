package org.livefx.dependency

class Dependencies(protected var _children: List[Dependency]) extends Dependency {
  def children: List[Dependency] = this.synchronized(_children)
  
  def children_=(list: List[Dependency]): Unit = this.synchronized {
    _children = list
  }
  
  override def depth: Int = 0

  protected def spoilSink: org.livefx.dependency.EventSink[org.livefx.script.Spoil] = ???

  def spoils: org.livefx.dependency.Events[org.livefx.script.Spoil] = ???
}
