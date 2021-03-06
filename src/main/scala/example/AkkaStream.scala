package example

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

object Stream extends App {
  implicit lazy val system       = ActorSystem("QuickStart")
  implicit lazy val materializer = ActorMaterializer()
  implicit lazy val ec           = system.dispatcher

  lazy val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val in = Source(1 to 10)
    val out = Sink.onComplete { duration =>
      println(duration)
      system.terminate()
    }
    val out2 = Sink.foreach[Int](println(_))

    val bcast  = builder.add(Broadcast[Int](2))
    val bcast2 = builder.add(Broadcast[Int](2))
    val merge  = builder.add(Merge[Int](2))

    val f1, f2, f3, f4 = Flow[Int].map(_ + 10)

    in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> bcast2 ~> out
    bcast2 ~> out2
    bcast ~> f4 ~> merge
    ClosedShape
  })

  g.run()
}
