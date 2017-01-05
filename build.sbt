name := "sbt-frontend"
organization := "com.eltimn"
description := "sbt plugin for managing frontend code (node and npm, grunt, gulp, bower, etc.)"

sbtPlugin := true

scalacOptions := Seq("-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "com.github.eirslett" %% "sbt-slf4j"            % "0.1",
  "com.github.eirslett" %  "frontend-plugin-core" % "1.3",
  "net.liftweb"         %% "lift-common"          % "2.6.3"
)

enablePlugins(GitVersioning)
