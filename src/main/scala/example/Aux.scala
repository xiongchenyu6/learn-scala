import scala.language.higherKinds
import scalaz._
import Scalaz._

object console extends App {

  trait Apart[F]{
    type T
    type W[X]

    def apply(f: F): W[T]
  }

  object Apart {
    def apply[F](implicit apart: Apart[F]) = apart

    type Aux[FA, F[_], A] = Apart[FA]{ type T = A; type W[X] = F[X]  }

    implicit def mk[F[_], R]: Aux[F[R], F, R] = new Apart[F[R]]{
      type T = R
      type W[X] = F[X]

      def apply(f: F[R]): W[T] = f
    }
  }

  def mapZero[Thing, F[_], A](thing: Thing)
             (implicit apart: Apart.Aux[Thing, F, A],
              f: Functor[F],
              m: Monoid[A]): F[A] =
    f.map(apart(thing))(_ => m.zero)
    // Equal to apart(thing).map(_ ⇒ m.zero) fix error

  mapZero(Option(List("dsf")))
}
