import sbt._

object Dependencies {

  lazy val testD = Seq(
    "org.scalatest" %% "scalatest" % "latest.integration" % Test
  )

  lazy val devD = Seq(
    "org.typelevel" %% "cats-core" % "latest.integration",
    "org.typelevel" %% "cats-free" % "latest.integration",
    "org.typelevel" %% "cats-effect" % "latest.integration",
    "com.chuusai" %% "shapeless" % "latest.integration",
    "com.typesafe.akka" %% "akka-stream" % "latest.integration"
  )
}
