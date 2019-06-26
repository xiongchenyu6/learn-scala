import Dependencies._

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.example",
      scalaVersion := "2.13.0",
      version := "0.1.0-SNAPSHOT"
    )
  ),
  name := "Hello",
  libraryDependencies ++= testD ++ devD
)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-Xcheckinit", // for debugging only, see https://github.com/paulp/scala-faq/wiki/Initialization-Order
  "-Xverify",
  "-feature",
  "-target:jvm-1.8",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:existentials",
// "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)
ThisBuild / resolvers ++= Seq(
  Resolver.mavenLocal, // Search local maven first for dev
  Resolver.sonatypeRepo("snapshots")
)

scalafmtOnCompile := true
