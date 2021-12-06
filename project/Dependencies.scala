import sbt._

object Dependencies {

  lazy val testD = Seq(
    "org.scalatest" %% "scalatest" % "latest.integration" % Test
  )

  lazy val devD = Seq(
    "com.chuusai"                 %% "shapeless"            % "latest.integration",
    "com.typesafe.akka"           %% "akka-stream"          % "latest.integration",
    "org.scala-lang.modules"      %% "scala-swing"          % "latest.integration",
    "org.apache.directory.studio" % "org.apache.commons.io" % "latest.integration"
  )

  lazy val devDS = Seq(
    "org.typelevel" %% "cats-core"   % "early-semver",
    "org.typelevel" %% "cats-effect" % "early-semver",
    "org.typelevel" %% "cats-free"   % "early-semver"
  )
}
