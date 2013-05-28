package org.livefx

trait LiveEquiv[T] extends Any with Serializable {
  def equiv(x: Live[T], y: Live[T]): Live[Boolean]
}

object LiveEquiv {
  object Implicits {
    trait IntLiveEquiv extends LiveEquiv[Int] {
      override def equiv(x: Live[Int], y: Live[Int]): Live[Boolean] = new LiveBinding[Boolean] {
        val ref1 = (x.spoils + y.spoils).subscribeWeak((_, spoilEvent) => spoil(spoilEvent))
        
        protected def computeValue: Boolean = x.value == y.value
      }
    }
    implicit object IntLiveEquiv extends IntLiveEquiv
  }
}
