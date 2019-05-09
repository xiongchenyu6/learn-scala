package example
import scala.collection.mutable.Queue

object BadThreadPool extends App {

  private val tasks = Queue[() => Unit]()
  object Worker extends Thread {
    setDaemon(true)
    def poll() = tasks.synchronized {
      while (tasks.isEmpty) tasks.wait()
      tasks.dequeue()
    }
    override def run() = while (true) {
      val task = poll()
      task()
    }
  }
  Worker.start()
  def asynchronous(body: => Unit) = tasks.synchronized {
    tasks.enqueue(() => body)
    tasks.notify()
  }
  asynchronous { println("Hello ") }
  asynchronous { println("World!") }
  Thread.sleep(500)
}
