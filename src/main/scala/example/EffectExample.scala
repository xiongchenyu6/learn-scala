package example
import cats.effect.{Async, Effect, IO, SyncIO}
import cats.implicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object EffectExample extends App {
  val apiCall = Future.successful("I come from the Future!")

  val ioa: IO[String] =
    Async[IO].async { cb =>
      import scala.util.{Failure, Success}

      apiCall.onComplete {
        case Success(value) => cb(Right(value))
        case Failure(error) => cb(Left(error))
      }
    }

  println(ioa.unsafeRunSync())

  val task: IO[String] = IO { println("sdafsdfas") } >> IO({
    "Hello World!"
  })

  val ioa2: SyncIO[Unit] =
    Effect[IO].runAsync(task) {
      case Right(value) => IO(println(value))
      case Left(error)  => IO.raiseError(error)
    }

  Thread.sleep(1500)
  ioa2.unsafeRunSync()

}
