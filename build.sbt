name := "SpreadTheWorld"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
  "com.restfb" % "restfb" % "1.6.14",
  "com.github.ndeverge" %% "autoping-play2-plugin" % "0.1.1",
  "commons-io" % "commons-io" % "2.4"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.url("Autoping repository", url("http://ndeverge.github.com/autoping-play2-plugin/snapshots/"))(Resolver.ivyStylePatterns)
)

play.Keys.lessEntryPoints <<= baseDirectory { base =>
  (base / "app" / "assets" / "stylesheets"  * "main.less") +++
  (base / "app" / "assets" / "stylesheets"  * "variables.less") +++
    (base / "app" / "assets" / "stylesheets" * "*.less")
}

play.Project.playJavaSettings
