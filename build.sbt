import Dependencies._

val scalazVersion = "7.1.17"
lazy val root = (project in file(".")).
settings(
  inThisBuild(List(
    organization := "com.example",
    scalaVersion := "2.12.5",
    version      := "0.1.0-SNAPSHOT"
  )),
    name := "Hello",
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % scalazVersion,
      "org.scalaz" %% "scalaz-effect" % scalazVersion,
      "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
      "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test",
      scalaTest % Test,
      "com.typesafe.akka" %% "akka-stream" % "2.5.11"
    )
  )

scalacOptions += "-feature"

initialCommands in console := "import scalaz._, Scalaz._"
