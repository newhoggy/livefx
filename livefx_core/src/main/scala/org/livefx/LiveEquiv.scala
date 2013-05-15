package org.livefx

trait LiveEquiv[T] extends Any with Serializable {
  def equiv(x: LiveValue[T], y: LiveValue[T]): LiveValue[Boolean]
}

object LiveEquiv {
  object Implicits {
    trait IntLiveEquiv extends LiveEquiv[Int] {
      override def equiv(x: LiveValue[Int], y: LiveValue[Int]): LiveValue[Boolean] = new LiveBinding[Boolean] {
        val ref1 = (x.spoils + y.spoils).subscribeWeak((_, spoilEvent) => spoil(spoilEvent))
        
        protected def computeValue: Boolean = x.value == y.value
      }
    }
    implicit object IntLiveEquiv extends IntLiveEquiv
  }
}
