import sbt.Keys._
import sbt._

object Build extends sbt.Build {
  val scalaz_core               = "org.scalaz"      %%  "scalaz-core"               % "7.2.3"
  val scalaz_concurrent         = "org.scalaz"      %%  "scalaz-concurrent"         % "7.2.3"
  
  val scalacheck                = "org.scalacheck"  %%  "scalacheck"                % "1.13.0"
  val scalaz_scalacheck_binding = "org.scalaz"      %%  "scalaz-scalacheck-binding" % "7.2.3"
  val specs2_core               = "org.specs2"      %%  "specs2-core"               % "3.7.2"
  val specs2_scalacheck         = "org.specs2"      %%  "specs2-scalacheck"         % "3.7.2"

  implicit class ProjectOps(self: Project) {
    def standard = {
      self
          .settings(scalacOptions in Test ++= Seq("-Yrangepos"))
          .settings(publishTo := Some("Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(isSnapshot := true)
    }

    def notPublished = self.settings(publish := {}).settings(publishArtifact := false)

    def libs(modules: ModuleID*) = self.settings(libraryDependencies ++= modules)

    def testLibs(modules: ModuleID*) = self.libs(modules.map(_ % "test"): _*)
  }

  lazy val `livefx-core` = Project(id = "livefx-core", base = file("livefx-core"))
      .standard
      .libs(scalaz_core, scalaz_concurrent)
      .testLibs(specs2_core, scalaz_scalacheck_binding, scalacheck)

  lazy val `livefx-event` = Project(id = "livefx-event", base = file("livefx-event"))
      .standard
      .libs(scalaz_core, scalaz_concurrent)
      .testLibs(specs2_core, scalaz_scalacheck_binding, scalacheck)
      .dependsOn(`livefx-core`)

  lazy val `livefx-value` = Project(id = "livefx-value", base = file("livefx-value"))
      .standard
      .libs(scalaz_core, scalaz_concurrent)
      .testLibs(specs2_core, scalaz_scalacheck_binding, scalacheck)
      .dependsOn(`livefx-event`)

  lazy val all = Project(id = "all", base = file("."))
      .notPublished
      .aggregate(`livefx-core`)
}
