package org.livefx.volume

case class Root[+A](child: Tree[A] = Leaf()) {
  def size: Int = child.size
  def count: Int = child.count
  
  def insert[B >: A](index: Int, value: B): Root[B] = child.insert(index, value) match {
    case newChild =>
      if (newChild.count > 3) {
        Root(Branch(newChild.split))
      } else {
        Root(newChild)
      }
  }
  
  def update[B >: A](index: Int, value: B): Root[B] = {
    println(s"--> $this.update($index, $value)")
    Root(child.update(index, value))
  }
  
  def remove(index: Int): (A, Root[A]) = child.remove(index) match {
    case (removed, newChild) => (removed, Root(newChild))
  }
  
  def toList[B >: A](acc: List[B] = Nil): List[B] = child.toList(acc)
}
