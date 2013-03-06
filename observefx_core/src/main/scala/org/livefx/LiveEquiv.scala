package org.livefx

trait LiveEquiv[T] extends Any with Serializable {
  def equiv(x: LiveValue[T], y: LiveValue[T]): LiveValue[Boolean]
}

object LiveEquiv {
  object Implicits {
    trait IntLiveEquiv extends LiveEquiv[Int] {
      override def equiv(x: LiveValue[Int], y: LiveValue[Int]): LiveValue[Boolean] = new LiveBinding[Boolean] {
        val ref1 = x.spoils.subscribeWeak((_, _) => spoil)
        val ref2 = y.spoils.subscribeWeak((_, _) => spoil)
        
        protected def computeValue: Boolean = x.value == y.value
      }
    }
    implicit object IntLiveEquiv extends IntLiveEquiv
  }
}