package throwaway
import java.io.File

import akka.stream.alpakka.csv.scaladsl.{CsvFormatting, CsvParsing}
import akka.stream.scaladsl.{Broadcast, FileIO, Source}
import ParseAddressToLindkinFormatHelper._

import akka.stream.{ActorMaterializer, ClosedShape}
import models.LocationInfo

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import akka.stream._
import akka.stream.scaladsl._

import akka.NotUsed
import akka.actor.ActorSystem
import akka.util.ByteString
import java.nio.file.Paths
import play.api.libs.json.Json

object expandBloombergData extends App {

  implicit val system = ActorSystem("webScraper")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    import expandBloombergDataHelper._

    val csvFiles: List[File] = getListOfFiles("bloombergCompanies")

    val in: Source[ByteString, NotUsed] = csvFiles
      .map { file =>
        FileIO.fromPath(file.toPath)
      }
      .foldLeft(Source.empty[ByteString])(_ concat _)

    val bcast = builder.add(Broadcast[Either[List[ByteString], List[String]]](3))

    val cleanAndExit = Sink.onComplete { duration =>
      system.terminate()
      println(duration)
    }
    val log = Sink.foreach[Either[List[ByteString], List[String]]](println(_))

    val parsedCsvSink = FileIO.toPath(Paths.get("parsedBloomberCompanies.csv"))

    val parsedFailCsvSink = FileIO.toPath(Paths.get("parseFailedBloomberCompanies.csv"))

    val csvReaderFlow =
      CsvParsing.lineScanner().filter(_(3).utf8String != "Cant find").filter(_(3).utf8String != "Can't find")
    val csvWriterFlow = CsvFormatting.format()

    val flow = Flow[List[ByteString]].map {
      byteString =>
        val url = byteString.lift(0).get.utf8String
        val companyName = byteString.lift(1).getOrElse(ByteString.empty).utf8String
        val country = byteString.lift(2).getOrElse(ByteString.empty).utf8String
        val domain = byteString.lift(3).getOrElse(ByteString.empty).utf8String
        expandBloombergDataHelper.parseUrl(url, country) match {
          case Some((address, phone)) => Right(List(url, companyName, Json.toJson(address).toString(), domain, phone))
          case _ => Left(byteString)
        }
    }

    val eitherSplit = builder.add(splitEither[List[ByteString], List[String]])

    in ~> csvReaderFlow ~> flow ~> bcast ~> eitherSplit.in
    bcast ~> log
    bcast ~> cleanAndExit
    eitherSplit.right ~> csvWriterFlow ~> parsedCsvSink
    eitherSplit.left.map(_.map(_.utf8String)) ~> csvWriterFlow ~> parsedFailCsvSink
    ClosedShape
  })

  graph.run()
}

object expandBloombergDataHelper {

  def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def parseUrl(url: String, country: String): Option[(LocationInfo, String)] = {
    val browser = JsoupBrowser()
    try {
      val doc = browser.get(url)
      val parsed = for {
        address <- doc >?> texts("div[itemprop=address]>p")
        phone <- doc >?> allText("p[itemprop=telephone]")
      } yield (address.fold("") { _ ++ " " ++ _ }, phone)

      parsed.map {
        case (address, phone) =>
          parseAddress(address, Some(country)) -> phone
      }
    } catch {
      case _: Exception => {
        None
      }
    }
  }

  def splitEither[L, R]: Graph[EitherFanOutShape[Either[L, R], L, R], NotUsed] =
    GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val input = b.add(Flow[Either[L, R]])
      val bcast = b.add(Broadcast[Either[L, R]](outputPorts = 2))
      val leftOut = b.add(Flow[Either[L, R]].collect { case Left(l) => l })
      val rightOut = b.add(Flow[Either[L, R]].collect { case Right(r) => r })

      input ~> bcast ~> leftOut
      bcast ~> rightOut

      new EitherFanOutShape(input.in, leftOut.out, rightOut.out)
    }
}

class EitherFanOutShape[In, L, R](_init: FanOutShape.Init[In]) extends FanOutShape[In](_init) {
  def this(name: String) = this(FanOutShape.Name[In](name))
  def this(in: Inlet[In], left: Outlet[L], right: Outlet[R]) = this(FanOutShape.Ports(in, left :: right :: Nil))

  override protected def construct(init: FanOutShape.Init[In]): FanOutShape[In] = new EitherFanOutShape(init)
  override def deepCopy(): EitherFanOutShape[In, L, R] = super.deepCopy().asInstanceOf[EitherFanOutShape[In, L, R]]

  val left: Outlet[L] = newOutlet[L]("left")
  val right: Outlet[R] = newOutlet[R]("right")
}
