lazy val commonSettings: Seq[Setting[_]] = Seq(
  git.baseVersion in ThisBuild := "0.1.0",
  organization in ThisBuild := "com.eltimn"
)

lazy val sbt_frontend = (project in file("."))
  .enablePlugins(GitVersioning)
  .settings(commonSettings: _*)
  .settings(
    sbtPlugin := true,
    name := "sbt-frontend",
    description := "sbt plugin for managing frontend code (node and npm, grunt, gulp, bower, etc.)",
    licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE")),
    scalacOptions := Seq("-deprecation", "-unchecked"),
    libraryDependencies ++= Seq(
      "com.github.eirslett" %% "sbt-slf4j"            % "0.1",
      "com.github.eirslett" %  "frontend-plugin-core" % "0.0.27",
      "net.liftweb"         %% "lift-common"          % "2.6.3"
    ),
    publishArtifact in (Compile, packageBin) := true,
    publishArtifact in (Test, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := true
  )


