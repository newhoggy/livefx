import sbt._
import Keys._

object XSDK extends Build {
  lazy val root = Project(id = "observesfx", base = file(".")) aggregate(livefx_core, livefx_bindings)

  lazy val livefx_core = Project(id = "livefx_core", base = file("livefx_core"))

  lazy val livefx_bindings = Project(id = "livefx_bindings", base = file("livefx_bindings")) dependsOn(livefx_core)
}

