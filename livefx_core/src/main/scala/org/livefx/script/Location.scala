package org.livefx.script

sealed abstract class Location

case object Start extends Location
case object End extends Location
case object NoLo extends Location
case class Index(n: Int) extends Location
