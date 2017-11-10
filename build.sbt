name := "sbt-frontend"
organization := "com.eltimn"
description := "sbt plugin for managing frontend code (node and npm, grunt, gulp, bower, etc.)"

sbtPlugin := true

scalaVersion := "2.12.2"

scalacOptions := Seq("-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "com.github.eirslett" %% "sbt-slf4j"            % "0.2",
  "com.github.eirslett" %  "frontend-plugin-core" % "1.3",
  "net.liftweb"         %% "lift-common"          % "3.1.1"
)

enablePlugins(GitVersioning)
