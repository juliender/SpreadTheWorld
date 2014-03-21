name := "SpreadTheWorld"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
  "com.restfb" % "restfb" % "1.6.14"
)

resolvers += Resolver.sonatypeRepo("snapshots")

play.Project.playJavaSettings
