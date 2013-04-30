addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.1")

resolvers += Resolver.url("sbt-plugin-snapshots", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
 
addSbtPlugin("com.typesafe.sbt" % "sbt-proguard" % "0.2.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8")

//addSbtPlugin("no.vedaadata" %% "sbt-javafx" % "0.4.1-SNAPSHOT")

