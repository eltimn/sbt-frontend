// com.eltimn/sbt-slf4j/scala_2.12/sbt_1.0/1.0.0/jars/sbt-slf4j.jar

name := "sbt-frontend"
organization := "com.eltimn"
description := "sbt plugin for managing frontend code (node and npm, grunt, gulp, bower, etc.)"

sbtPlugin := true

scalaVersion := "2.12.4"

scalacOptions := Seq("-deprecation", "-unchecked")

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.eltimn"          %% "sbt-slf4j"            % "1.0.4",
  "com.github.eirslett" %  "frontend-plugin-core" % "1.6",
  "net.liftweb"         %% "lift-common"          % "3.1.1"
)

enablePlugins(GitVersioning)
