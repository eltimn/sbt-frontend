addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

libraryDependencies <+= (sbtVersion) { sv =>
  "org.scala-sbt" % "scripted-plugin" % sv
}
