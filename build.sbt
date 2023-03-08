import Dependencies._

ThisBuild / versionScheme := Some("early-semver")

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.example",
      scalaVersion := "3.1.2-RC1",
      version      := "0.1.0-SNAPSHOT"
    )
  ),
  name := "Hello",
  libraryDependencies ++= testD ++ devD,
  libraryDependencySchemes ++= devDS
)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:existentials",
// "-Xfatal-warnings",
  "-language:postfixOps"
)
ThisBuild / resolvers ++= Seq(
  Resolver.mavenLocal, // Search local maven first for dev
  Resolver.sonatypeRepo("snapshots")
)
