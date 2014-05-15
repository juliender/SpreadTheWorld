name := "SpreadTheWorld"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  filters,
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "ws.securesocial" %% "securesocial" % "2.1.x-SNAPSHOT",
  "com.restfb" % "restfb" % "1.6.14",
  "com.github.ndeverge" %% "autoping-play2-plugin" % "0.1.1",
  "commons-io" % "commons-io" % "2.4",
  "com.cloudinary" % "cloudinary" % "1.0.8",
  "com.github.mumoshu" %% "play2-memcached" % "0.3.0.2"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.url("Autoping repository", url("http://ndeverge.github.com/autoping-play2-plugin/snapshots/"))(Resolver.ivyStylePatterns),
  "Spy Repository" at "http://files.couchbase.com/maven2"
)

play.Keys.lessEntryPoints <<= baseDirectory { base =>
  (base / "app" / "assets" / "stylesheets"  * "main.less") +++
  (base / "app" / "assets" / "stylesheets"  * "variables.less") +++
    (base / "app" / "assets" / "stylesheets" * "*.less")
}

play.Project.playJavaSettings
