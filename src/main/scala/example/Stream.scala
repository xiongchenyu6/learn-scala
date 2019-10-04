package example

import cats.effect.{ExitCode, IO}

object Stream {

  val eff  = Stream.eval(IO { println("BEING RUN!!"); 1 + 1 })
  val eff1 = Stream(1, 2, 3).merge(Stream.eval(IO { Thread.sleep(200); 4 }))
  trait Connection {
    def readBytes(onSuccess: Array[Byte] => Unit, onFailure: Throwable => Unit): Unit

    // or perhaps
    def readBytesE(onComplete: Either[Throwable, Array[Byte]] => Unit): Unit =
      readBytes(bs => onComplete(Right(bs)), e => onComplete(Left(e)))

    override def toString = "<connection>"
  }

  val c = new Connection {
    def readBytes(onSuccess: Array[Byte] => Unit, onFailure: Throwable => Unit): Unit = {
      Thread.sleep(200)
      onSuccess(Array(0, 1, 2))
    }
  }
  val bytes: IO[Array[Byte]] = cats.effect.Async[IO].async[Array[Byte]] { cb: Either[Throwable, Array[Byte]] => Unit =>
    c.readBytesE(cb)
  }

  println(eff1.compile.toVector.unsafeRunSync())
  eff.compile.last.map(_ => ExitCode.Success)
}
