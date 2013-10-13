scalaVersion in ThisBuild := "2.10.0"

EclipseKeys.withSource := true

javaHome in ThisBuild := Some(file {
   Option(System.getenv("JAVA_HOME")).getOrElse {
     println("Warning: JAVA_HOME not defined")
     "."
   }
})

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomExtra := (
  <url>http://timesprint.com/rillit-scalaz</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:newhoggy/livefx.git</url>
    <connection>scm:git:git@github.com:newhoggy/rillit-scalaz.git</connection>
  </scm>
  <developers>
    <developer>
      <id>newhoggy</id>
      <name>John Ky</name>
      <url>http://timesprint.com</url>
    </developer>
  </developers>)

autoCompilerPlugins := true

