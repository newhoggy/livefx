import sbt._
import Keys._

object XSDK extends Build {
  lazy val root = Project(id = "observesfx", base = file(".")) aggregate(observefx_core)

  lazy val observefx_core = Project(id = "observefx_core", base = file("observefx_core"))
}

