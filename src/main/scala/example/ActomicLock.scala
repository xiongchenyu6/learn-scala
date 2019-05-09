package example
import java.util.concurrent.atomic._
import scala.concurrent.ExecutionContext

object AtomicLock extends App {
  def execute(body: => Unit) = ExecutionContext.global.execute(
    new Runnable { def run() = body }
  )
  private val lock = new AtomicBoolean(false)
  def mySynchronized(body: => Unit): Unit = {
    while (!lock.compareAndSet(false, true)) {}
    try body
    finally lock.set(false)
  }
  var count = 0
  for (i <- 0 until 10) execute { mySynchronized { count += 1 } }
  Thread.sleep(1000)
  println(s"Count is: $count")
}
