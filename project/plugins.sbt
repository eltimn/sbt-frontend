addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

libraryDependencies <+= (sbtVersion) { sv =>
  "org.scala-sbt" % "scripted-plugin" % sv
}

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
