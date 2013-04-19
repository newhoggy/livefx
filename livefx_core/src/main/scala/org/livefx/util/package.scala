package org.livefx

//package object util {
//  implicit class RichEither[A](in: Either[A, A]) {
//    def either[B](f: A => B): B = in match {
//      case scala.Left(x) => f(x)
//      case scala.Right(x) => f(x)
//    }
//  }
//  
//  object LeftOrRight {
//    def unapply[A](value: Either[A, A]): Option[A] = value match {
//      case scala.Left(x) => Some(x)
//      case scala.Right(x) => Some(x)
//    }
//  }
//}
