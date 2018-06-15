name := "hikvision-archive-parser"
organization := "net.jsfwa"

version := "0.1.6"

scalaVersion := "2.12.4"

lazy val root = project in file(".")


libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.18.0",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.11" % Test,
  "com.typesafe" % "config" % "1.3.3"
)

scalaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test"

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.ivy2/local")))

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

sources in (Compile,doc) := Seq.empty