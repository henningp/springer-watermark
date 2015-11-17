name := "springer-watermark"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  specs2 % Test
)

scalacOptions in Test ++= Seq("-Yrangepos")