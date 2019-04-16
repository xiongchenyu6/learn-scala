import Dependencies._

lazy val root = (project in file(".")).
settings(
  inThisBuild(List(
    organization := "com.example",
    scalaVersion := "2.12.8",
    version      := "0.1.0-SNAPSHOT"
  )),
    name := "Hello",
    libraryDependencies ++= testD ++ devD
)

scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", 
    "UTF-8",
    "-feature",
    "-unchecked",
    "-language:higherKinds",
    "-language:implicitConversions"
)
