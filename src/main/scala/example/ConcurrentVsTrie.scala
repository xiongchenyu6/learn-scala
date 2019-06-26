package example

import java.util.concurrent.ConcurrentHashMap

import scala.jdk.CollectionConverters._
import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext

object CocurrentVsTrie extends App {

  def execute(body: => Unit): Unit = ExecutionContext.global.execute(
    new Runnable { def run(): Unit = body }
  )
  val names = new ConcurrentHashMap[String, Int]().asScala
  names("Johnny") = 0; names("Jane") = 0; names("Jack") = 0

  execute {
    for (n <- names) println(s"name: $n")
  }

  execute {
    for (n <- 0 until 10) names(s"John $n") = n
  }

  val names1 = new TrieMap[String, Int]
  names1("Janice") = 0; names1("Jill") = 0
  execute {
    println("snapshot time!")
    for (n <- names1.map(_._1).toSeq.sorted)
      println(s"name: $n")
  }
  execute { for (n <- 10 until 100) names1(s"John $n") = n }

  Thread.sleep(1000)

}
