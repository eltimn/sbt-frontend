version := "0.1"

scalaVersion := "2.11.7"

FrontendKeys.nodeInstallDirectory := baseDirectory.value  / ".frontend"

FrontendKeys.nodeWorkingDirectory := baseDirectory.value  / ".frontend"

enablePlugins(FrontendPlugin)
