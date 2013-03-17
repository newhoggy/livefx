import sbt._
import Keys._

object XSDK extends Build {
  lazy val root = Project(id = "observesfx", base = file(".")) aggregate(livefx_core, livefx_coretest)

  lazy val livefx_core = Project(id = "livefx_core", base = file("livefx_core"))

  lazy val livefx_coretest = Project(id = "livefx_coretest", base = file("livefx_coretest")) dependsOn(livefx_core)
}

