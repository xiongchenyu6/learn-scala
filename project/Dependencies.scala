import sbt._

object Dependencies {

  lazy val testD = Seq(
    "org.scalatest" %% "scalatest" % "latest.integration" % Test
     )

  lazy val devD = Seq(
    "org.typelevel"               % "shapeless3-deriving_3" % "3.0.4",
    "com.typesafe.akka"           %% "akka-stream"          % "latest.integration",
    "org.apache.directory.studio" % "org.apache.commons.io" % "latest.integration",
    "org.typelevel"               %% "cats-core"            % "2.7.0",
    "org.typelevel"               %% "cats-free"            % "2.7.0",
    "org.typelevel"               %% "cats-effect"          % "3.3.0",
                "co.fs2"                      %% "fs2-core"             % "3.2.2"
  )

  lazy val devDS = Seq(
            )
}
