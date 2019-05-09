package example

import shapeless._
import shapeless.ops.hlist.IsHCons
import shapeless.syntax.singleton._
import shapeless.labelled.{KeyTag, FieldType}
import shapeless.labelled.field
import shapeless.Witness

object Generi {

  case class Employee(name: String, number: Int, manager: Boolean)

  sealed trait Tree[A]
  object CsvEncoder {
    // "Summoner" method
    def apply[A](implicit enc: CsvEncoder[A]): CsvEncoder[A] =
      enc

    // "Constructor" method
    def instance[A](func: A => List[String]): CsvEncoder[A] =
      new CsvEncoder[A] {
        def encode(value: A): List[String] =
          func(value)
      }

    // Globally visible type class instances
  }
  case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
  case class Leaf[A](value: A)                        extends Tree[A]
  trait CsvEncoder[A] {
    def encode(value: A): List[String]
  }

  def createEncoder[A](func: A => List[String]): CsvEncoder[A] =
    new CsvEncoder[A] {
      def encode(value: A): List[String] = func(value)
    }

  implicit val stringEncoder: CsvEncoder[String] =
    createEncoder(str => List(str))

  implicit val intEncoder: CsvEncoder[Int] =
    createEncoder(num => List(num.toString))

  implicit val booleanEncoder: CsvEncoder[Boolean] =
    createEncoder(bool => List(if (bool) "yes" else "no"))

  implicit val hnilEncoder: CsvEncoder[HNil] =
    createEncoder(hnil => Nil)

  val reprEncoder: CsvEncoder[String :: Int :: Boolean :: HNil] =
    implicitly

  implicit val cnilEncoder: CsvEncoder[CNil] =
    createEncoder(cnil => throw new Exception("Inconceivable!"))

  implicit def hlistEncoder[H, T <: HList](
      implicit
      hEncoder: Lazy[CsvEncoder[H]], // wrap in Lazy
      tEncoder: CsvEncoder[T]
  ): CsvEncoder[H :: T] = createEncoder {
    case h :: t =>
      hEncoder.value.encode(h) ++ tEncoder.encode(t)
  }

  implicit def coproductEncoder[H, T <: Coproduct](
      implicit
      hEncoder: Lazy[CsvEncoder[H]], // wrap in Lazy
      tEncoder: CsvEncoder[T]
  ): CsvEncoder[H :+: T] = createEncoder {
    case Inl(h) => hEncoder.value.encode(h)
    case Inr(t) => tEncoder.encode(t)
  }

  implicit def genericEncoder[A, R](
      implicit
      gen: Generic.Aux[A, R],
      rEncoder: Lazy[CsvEncoder[R]] // wrap in Lazy
  ): CsvEncoder[A] = createEncoder { value =>
    rEncoder.value.encode(gen.to(value))
  }

  def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
    values.map(value => enc.encode(value).mkString(",")).mkString("\n")

  reprEncoder.encode("abc" :: 123 :: true :: HNil)

  writeCsv(List(Employee("string", 1, true)))

  CsvEncoder[Tree[Int]]

  def getRepr[A](value: A)(implicit gen: Generic[A]) =
    gen.to(value)
  case class Vec(x: Int, y: Int)
  case class Rect(origin: Vec, size: Vec)

  getRepr(Vec(1, 2))
// res1: Int :: Int :: shapeless.HNil = 1 :: 2 :: HNil

  getRepr(Rect(Vec(0, 0), Vec(5, 5)))

  trait Second[L <: HList] {
    type Out
    def apply(value: L): Out
  }

  object Second {
    type Aux[L <: HList, O] = Second[L] { type Out = O }

    def apply[L <: HList](implicit inst: Second[L]): Aux[L, inst.Out] =
      inst
  }
  import Second._

  implicit def hlistSecond[A, B, Rest <: HList]: Aux[A :: B :: Rest, B] =
    new Second[A :: B :: Rest] {
      type Out = B
      def apply(value: A :: B :: Rest): B =
        value.tail.head
    }
  val second1 = Second[String :: Boolean :: Int :: HNil]

  val second2 = Second[String :: Int :: Boolean :: HNil]

  def lastField[A, Repr <: HList](input: A)(
      implicit
      gen: Generic.Aux[A, Repr],
      second: Second[Repr]
  ): second.Out = second.apply(gen.to(input))

  lastField(Rect(Vec(1, 2), Vec(3, 4)))

  def getWrappedValue[A, Repr <: HList, Head](in: A)(
      implicit
      gen: Generic.Aux[A, Repr],
      isHCons: IsHCons.Aux[Repr, Head, HNil]
  ): Head = gen.to(in).head

  case class Wrapper(value: Int)

  getWrappedValue(Wrapper(42))

  val someNumber = 123

  val numCherries = "numCherries" ->> someNumber

  trait Cherries

  field[Cherries](123)

// Get the tag from a tagged value:
  def getFieldName[K, V](value: FieldType[K, V])(implicit witness: Witness.Aux[K]): K =
    witness.value

  getFieldName(numCherries)

// Get the untagged type of a tagged value:
  def getFieldValue[K, V](value: FieldType[K, V]): V =
    value

  getFieldValue(numCherries)

}

import Generi._
