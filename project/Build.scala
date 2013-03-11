import sbt._
import Keys._

object XSDK extends Build {
  lazy val root = Project(id = "observesfx", base = file(".")) aggregate(livefx_core)

  lazy val livefx_macro = Project(id = "livefx_macro", base = file("livefx_macro"))

  lazy val livefx_core = Project(id = "livefx_core", base = file("livefx_core")) dependsOn(livefx_macro)
}

