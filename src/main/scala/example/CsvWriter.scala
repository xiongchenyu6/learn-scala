package example

trait CsvWriter[A] {

  def toCsvString(a: A): String

}

case class Book(
    isbn: String,
    author: String
)

case class Cat(
    color: String
)

object CsvWriter {

  def apply[A](implicit cw: CsvWriter[A]): CsvWriter[A] = cw

  object ops {
    def toCsvString[A: CsvWriter](a: A) = CsvWriter[A].toCsvString(a)
    implicit class CsvWriterOps[A: CsvWriter](a: A) {
      def toCsvString = CsvWriter[A].toCsvString(a)
    }
  }

  implicit val bookToCsvString: CsvWriter[Book] = book ⇒ book.isbn ++ ", " ++ book.author
}

trait CsvFormatter[A] {
  def writeCsv(a: A): Seq[String]
  def readCsv(s: Seq[String]): A
}

object CsvFormatter {
  def apply[A](implicit cf: CsvFormatter[A]): CsvFormatter[A] = cf

  object ops {
    def writeCsv[A: CsvFormatter](a: A)          = CsvFormatter[A].writeCsv(a)
    def readCsv[A: CsvFormatter](a: Seq[String]) = CsvFormatter[A].readCsv(a)
    implicit class CsvFormatterOps[A: CsvFormatter](a: A) {
      def writeCsv                = CsvFormatter[A].writeCsv(a)
      def readCsv(s: Seq[String]) = CsvFormatter[A].readCsv(s)
    }
  }
  implicit val bookCsvFormatter = new CsvFormatter[Book] {
    def writeCsv(a: Book): Seq[String] = Seq(a.isbn, a.author)
    def readCsv(s: Seq[String]): Book  = Book(s(0), s(1))
  }
}
object CSVmain extends App {

  import CsvWriter._
  import CsvWriter.ops._
  import CsvFormatter._
  import CsvFormatter.ops._

  def ttt() = {
    val b = Book("3412", "Freeman")
    val a = b.writeCsv
    val f = readCsv(a)
    println(b.writeCsv)
    println(f)
    val c                               = Cat("Yellow")
    implicit val catCsv: CsvWriter[Cat] = cat ⇒ s"cat ${cat.color}"
    println(c.toCsvString)
  }

}
