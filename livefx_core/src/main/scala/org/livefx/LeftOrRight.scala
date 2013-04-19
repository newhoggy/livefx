package org.livefx

object LeftOrRight {
  def unapply[A](value: Either[A, A]): Option[A] = value match {
    case scala.Left(x) => Some(x)
    case scala.Right(x) => Some(x)
  }
}
