scalaVersion in ThisBuild := "2.10.0"

javaHome in ThisBuild := Some(file(System.getenv("JAVA_HOME")))

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))
