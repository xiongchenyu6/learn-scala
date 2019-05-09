package example

object SynchronizedGuardedBlocks extends App {
  def thread(body: => Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }
  val lock                    = new AnyRef
  var message: Option[String] = None
  val greeter = thread {
    lock.synchronized {
      while (message == None) lock.wait()
      println(message.get)
    }
  }
  lock.synchronized {
    message = Some("Hello!")
    lock.notify()
  }
  greeter.join()
}
