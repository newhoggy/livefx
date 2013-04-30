javaHome := Some(file(System.getenv("JAVA_HOME")))

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

libraryDependencies += "com.typesafe" %% "scalalogging-slf4j" % "1.0.1"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.4"

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

proguardSettings

ProguardKeys.options in Proguard ++= Seq("-dontnote", "-dontwarn", "-ignorewarnings")

ProguardKeys.options in Proguard += ProguardOptions.keepMain("org.livefx.*")

// From http://www.scala-sbt.org/release/docs/Community/Using-Sonatype.html
// https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
// https://github.com/harrah/xsbt/wiki/Publishing

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
  <url>http://timesprint.com/livefx_core</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:newhoggy/livefx_core.git</url>
    <connection>scm:git:git@github.com:newhoggy/livefx_core.git</connection>
  </scm>
  <developers>
    <developer>
      <id>newhoggy</id>
      <name>John Ky</name>
      <url>http://timesprint.com</url>
    </developer>
  </developers>)

