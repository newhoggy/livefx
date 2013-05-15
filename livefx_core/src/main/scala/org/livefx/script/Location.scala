package org.livefx.script

sealed abstract class Location

case class Start(n: Int) extends Location
case class End(n: Int) extends Location
case object NoLo extends Location
