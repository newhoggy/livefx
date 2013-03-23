import sbt._
import Keys._

object XSDK extends Build {
  lazy val buildSettings = Seq(
      organization := "com.github.kevinwright.macroflection",
      scalaVersion := "2.10.1",
      scalacOptions := Seq("-feature", "-deprecation", "-unchecked", "-Xlint", "-Yrangepos", "-encoding", "utf8"),
      scalacOptions in (console) += "-Yrangepos"
  )

  lazy val commonSettings = Defaults.defaultSettings ++ buildSettings

  lazy val root = Project(id = "observesfx", base = file(".")).aggregate(livefx_core, livefx_bindings).settings(commonSettings: _*)

  lazy val livefx_core = Project(id = "livefx_core", base = file("livefx_core")).settings(commonSettings: _*)

  lazy val livefx_bindings = Project(id = "livefx_bindings", base = file("livefx_bindings")).settings(commonSettings: _*).dependsOn(livefx_core)
}

