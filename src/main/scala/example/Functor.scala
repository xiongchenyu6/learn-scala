import scalaz._
import Scalaz._


object Saaaaa extends App {

  (((_: Int) * 3) map {_ + 100}) (1)
  val a =  Functor[List].lift {(_: Int) * 2}
  a(List(3))
  List(1, 2, 3) >| "x"
  List(1).fpair
  1.some
  1.point[List]
  9.some <*> {(_: Int) + 3}.some
  (3.some |@| 5.some) {_ + _}
  List(1, 2, 3) <*> List((_: Int) * 0, (_: Int) + 100, (x: Int) => x * x)

  //[(\x -> x*0),(\x -> x+100),(\x -> x+1)] <*> [1,2,3]
  def sequenceA[F[_]: Applicative, A](list: List[F[A]]): F[List[A]] = list match { case Nil => (Nil: List[A]).point[F]
    case x :: xs => (x |@| sequenceA(xs)) {_ :: _}
  }

  sequenceA(List(1.some, 2.some))

  sealed trait KiloGram
  def KiloGram[A](a: A): A @@ KiloGram = Tag[A, KiloGram](a)
  //infix notation of scalaz.@@[A, KiloGram]

  val mass = KiloGram(20.0)

  2 * Tag.unwrap(mass)

}
