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
    scalacOptions := Seq("-deprecation", "-unchecked"),
    libraryDependencies ++= Seq(
      "com.github.eirslett" %% "sbt-slf4j"            % "0.1",
      "com.github.eirslett" %  "frontend-plugin-core" % "0.0.27",
      "net.liftweb"         %% "lift-common"          % "2.6.3"
    ),
    publishArtifact in (Compile, packageBin) := true,
    publishArtifact in (Test, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := true,
    pomExtra := {
      <scm>
        <url>git@github.com:eltimn/sbt-frontend.git</url>
        <connection>scm:git:git@github.com:eltimn/sbt-frontend.git</connection>
      </scm>
      <developers>
        <developer>
          <id>eltimn</id>
          <name>Tim Nelson</name>
          <url>https://github.com/eltimn</url>
        </developer>
      </developers>
    },
    homepage := Some(url("https://github.com/eltimn/sbt-frontend")),
    licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT"))),
    bintrayOrganization := Some("eltimn")
  )


