package test.livefx

import org.junit.Test

object Operators {
  trait Thing {
    def +=(that: Thing): Thing = Operators.+=(this, that)
    def :=(that: Thing): Thing = Operators.:=(this, that)
    def +(that: Thing): Thing = Operators.+(this, that)
    def *(that: Thing): Thing = Operators.*(this, that)
  }
  
  case object Thing extends Thing
  case class +=(l: Thing, r: Thing) extends Thing
  case class :=(l: Thing, r: Thing) extends Thing
  case class +(l: Thing, r: Thing) extends Thing
  case class *(l: Thing, r: Thing) extends Thing
}

class TestOperatorPrecedence {
  @Test
  def testPrecedence(): Unit = {
    import Operators._
    println(Thing += Thing + Thing)
    println(Thing := Thing * Thing)
    println(Thing + Thing += Thing)
    println(Thing * Thing := Thing)
  }
}
