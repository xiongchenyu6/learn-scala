package example

import java.util.concurrent.ForkJoinPool
object ThreadPool extends App:
  val executor = new ForkJoinPool
  executor.execute(new Runnable {
    def run() = println("This task is run asynchronously.")
  })
  Thread.sleep(500)
