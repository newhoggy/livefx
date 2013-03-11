scalaVersion in ThisBuild := "2.10.0"

javaHome in ThisBuild := Some(file {
   Option(System.getenv("JAVA_HOME")).getOrElse {
     println("Warning: JAVA_HOME not defined")
     "."
   }
})

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

