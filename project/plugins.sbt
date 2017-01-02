addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

libraryDependencies +=
  "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
