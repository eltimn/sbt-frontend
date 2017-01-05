bintrayReleaseOnPublish in ThisBuild := false

pomExtra := {
  <scm>
    <url>git@github.com:eltimn/sbt-frontend.git</url>
    <connection>scm:git:git@github.com:eltimn/sbt-frontend.git</connection>
  </scm>
  <developers>
    <developer>
      <id>eltimn</id>
      <name>Tim Nelson</name>
      <url>https://eltimn.com/</url>
    </developer>
  </developers>
}

publishArtifact in Test := false
homepage := Some(url("https://github.com/eltimn/sbt-frontend"))
licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT")))
bintrayOrganization := Some("eltimn")
