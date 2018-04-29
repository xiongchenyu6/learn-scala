import scalaz._
import Scalaz._

object console extends App {

  trait Foo[A] {
    type B
    def value: B
  }

  object Foo {
    type Aux[A0, B0] = Foo[A0] { type B = B0 }

    implicit def fi = new Foo[Int] {
      type B = String
      val value = "Foo"
    }
    implicit def fs = new Foo[String] {
      type B = Boolean
      val value = false
    }

  }

  def ciao[T, R](t: T)
          (implicit f: Foo.Aux[T, R],
        //   m: Monoid[R]): R = f.value
           m: Monoid[R]): f.B = f.value
  val res = ciao(2)
  println(s"res: ${res}")
}
