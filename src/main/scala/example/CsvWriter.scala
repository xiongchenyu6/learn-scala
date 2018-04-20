import scala.language.implicitConversions

trait CsvWriter[A] {

  def toCsvString(a : A): String

}

case class Book(
  isbn :String,
  author:String
)

case class Cat(
  color:String
)

object CsvWriter{

  def apply[A](implicit cw: CsvWriter[A]): CsvWriter[A] = cw

  object ops{
    def toCsvString[A: CsvWriter](a: A) = CsvWriter[A].toCsvString(a)
    implicit class CsvWriterOps[A : CsvWriter](a : A) {
      def toCsvString =  CsvWriter[A].toCsvString(a)
    }
  }

  implicit val bookToCsvString: CsvWriter[Book] = book ⇒ book.isbn ++ ", " ++ book.author
}

object Main extends App{

  import CsvWriter._
  import CsvWriter.ops._
  val b = Book("3412","Freeman")
  val a = b.toCsvString
  val c = Cat("Yellow")
  implicit val catCsv: CsvWriter[Cat] = cat ⇒ s"cat ${cat.color}"
  println(c.toCsvString)

}
