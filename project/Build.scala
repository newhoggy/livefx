import sbt._
import Keys._

object XSDK extends Build {
  lazy val buildSettings = Seq(
      organization := "com.timesprint",
      scalaVersion := "2.10.2",
      scalacOptions := Seq("-feature", "-deprecation", "-unchecked", "-Xlint", "-Yrangepos", "-encoding", "utf8"),
      scalacOptions in (console) += "-Yrangepos"
  )

  lazy val commonSettings = Defaults.defaultSettings ++ buildSettings

  lazy val root = Project(id = "livefx", base = file("."))
    .aggregate(livefx_core).settings(commonSettings: _*)
    .aggregate(livefx_jfx).settings(commonSettings: _*)

  lazy val livefx_core = Project(id = "livefx_core", base = file("livefx_core"))
    .settings(commonSettings: _*)

  lazy val livefx_jfx = Project(id = "livefx_jfx", base = file("livefx_jfx"))
    .settings(commonSettings: _*)
    .dependsOn(livefx_core)
}

