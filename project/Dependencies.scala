import sbt._

object Dependencies {

  val scalazVersion = "7.1.17"

  lazy val testD = Seq(
    "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test",
    "org.scalatest" %% "scalatest" % "3.0.4" % Test
  )

  lazy val devD = Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion,
    "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
    "com.chuusai" %% "shapeless" % "2.3.3",
    "com.typesafe.akka" %% "akka-stream" % "2.5.11"
  )
}
