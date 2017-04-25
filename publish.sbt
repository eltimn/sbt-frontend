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
licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt")))
bintrayOrganization := Some("eltimn")

publishArtifact in (Compile, packageBin) := true
publishArtifact in (Test, packageBin) := false
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := true
