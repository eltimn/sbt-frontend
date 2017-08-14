{
  sys.props.get("plugin.version").map { pluginVersion =>
    addSbtPlugin("com.eltimn" % "sbt-frontend" % pluginVersion)
  } getOrElse {
    throw new RuntimeException("""
      |The system property 'plugin.version' is not defined.
      |Specify this property using the scriptedLaunchOpts -D.""".stripMargin
    )
  }
}
