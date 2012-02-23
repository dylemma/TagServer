name := "TagServer"

scalaVersion := "2.9.1"

seq(webSettings: _*)

scalacOptions := "-deprecation" :: "-unchecked" :: Nil

fork in run := true

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-webkit" % "2.4-M4" % "compile->default",
  "net.liftweb" %% "lift-openid" % "2.4-M4" % "compile->default"
)


libraryDependencies ++= Seq(
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default",
  "org.scalatest" %% "scalatest" % "1.7.1" % "test",
  "commons-io" % "commons-io" % "2.1"
)

resolvers ++= Seq(
	"Orient Technologies Maven2 Repository" at "http://www.orientechnologies.com/listing/m2",
	"Tinkerpop" at "http://tinkerpop.com/maven2"
)

libraryDependencies ++= Seq(
	"com.orientechnologies" % "orientdb-core" % "1.0rc8",
	"com.tinkerpop.blueprints" % "blueprints-orient-graph" % "1.1"
)