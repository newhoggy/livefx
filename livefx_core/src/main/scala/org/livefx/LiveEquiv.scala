package org.livefx

import org.livefx.{dependency => dep}

trait LiveEquiv[T] extends Any with Serializable {
  def equiv(x: Live[T], y: Live[T]): Live[Boolean]
}

object LiveEquiv {
  object Implicits {
    trait IntLiveEquiv extends LiveEquiv[Int] {
      override def equiv(x: Live[Int], y: Live[Int]): Live[Boolean] = new Binding[Boolean] {
        override def dependency: dep.Live[Int] = (x.spoils.dependency max y.spoils.dependency).incremented
        val ref1 = (x.spoils | y.spoils).subscribe(spoilEvent => spoil(spoilEvent))
        
        protected def computeValue: Boolean = x.value == y.value
      }
    }
    implicit object IntLiveEquiv extends IntLiveEquiv
  }
}
