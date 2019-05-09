package example

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Random extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val launchMissiles: IO[String] = IO.raiseError(new Exception("boom!"))
    val runToBunker: IO[Unit]      = IO(println("To the bunker!!!"))

    (for {

      fiber <- IO.shift *> launchMissiles.start
      _ <- runToBunker.handleErrorWith { error =>
        // Retreat failed, cancel launch (maybe we should
        // have retreated to our bunker before the launch?)
        fiber.cancel *> IO.raiseError(error)
      }
      aftermath <- fiber.join
    } yield aftermath).map(_ => ExitCode.Success)

  }
}
