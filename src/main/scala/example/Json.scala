package example

import shapeless.{Witness, _}
import shapeless.labelled.FieldType
import shapeless.ops._

object Json {
  sealed trait JsonValue
  case class JsonObject(fields: List[(String, JsonValue)]) extends JsonValue
  case class JsonArray(items: List[JsonValue])             extends JsonValue
  case class JsonString(value: String)                     extends JsonValue
  case class JsonNumber(value: Double)                     extends JsonValue
  case class JsonBoolean(value: Boolean)                   extends JsonValue
  case object JsonNull                                     extends JsonValue

  trait JsonEncoder[A] {
    def encode(value: A): JsonValue
  }

  object JsonEncoder {
    def apply[A](implicit enc: JsonEncoder[A]): JsonEncoder[A] = enc
  }

  def createEncoder[A](func: A => JsonValue): JsonEncoder[A] =
    (value: A) => func(value)

  implicit val stringEncoder: JsonEncoder[String] =
    createEncoder(str => JsonString(str))

  implicit val doubleEncoder: JsonEncoder[Double] =
    createEncoder(num => JsonNumber(num))

  implicit val intEncoder: JsonEncoder[Int] =
    createEncoder(num => JsonNumber(num))

  implicit val booleanEncoder: JsonEncoder[Boolean] =
    createEncoder(bool => JsonBoolean(bool))

  implicit def listEncoder[A](implicit enc: JsonEncoder[A]): JsonEncoder[List[A]] =
    createEncoder(list => JsonArray(list.map(enc.encode)))

  implicit def optionEncoder[A](implicit enc: JsonEncoder[A]): JsonEncoder[Option[A]] =
    createEncoder(opt => opt.map(enc.encode).getOrElse(JsonNull))

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  val iceCream = IceCream("Sundae", 1, false)

  val gen = LabelledGeneric[IceCream].to(iceCream)

  trait JsonObjectEncoder[A] extends JsonEncoder[A] {
    def encode(value: A): JsonObject
  }

  def createObjectEncoder[A](fn: A => JsonObject): JsonObjectEncoder[A] =
    (value: A) => fn(value)

  implicit val hnilEncoder: JsonObjectEncoder[HNil] =
    createObjectEncoder(hnil => JsonObject(Nil))

  implicit def hlistObjectEncoder[K <: Symbol, H, T <: HList](
      implicit
      witness: Witness.Aux[K],
      hEncoder: Lazy[JsonEncoder[H]],
      tEncoder: JsonObjectEncoder[T]
  ): JsonObjectEncoder[FieldType[K, H] :: T] = {
    val fieldName: String = witness.value.name
    createObjectEncoder { hlist =>
      val head = hEncoder.value.encode(hlist.head)
      val tail = tEncoder.encode(hlist.tail)
      JsonObject((fieldName, head) :: tail.fields)
    }
  }

  implicit val cnilObjectEncoder: JsonObjectEncoder[CNil] =
    createObjectEncoder(cnil => throw new Exception("Inconceivable!"))

  implicit def coproductObjectEncoder[K <: Symbol, H, T <: Coproduct](
      implicit
      witness: Witness.Aux[K],
      hEncoder: Lazy[JsonEncoder[H]],
      tEncoder: JsonObjectEncoder[T]
  ): JsonObjectEncoder[FieldType[K, H] :+: T] = {
    val typeName = witness.value.name
    createObjectEncoder {
      case Inl(h) =>
        JsonObject(List(typeName -> hEncoder.value.encode(h)))

      case Inr(t) =>
        tEncoder.encode(t)
    }
  }

  implicit def genericObjectEncoder[A, H](
      implicit
      generic: LabelledGeneric.Aux[A, H],
      hEncoder: Lazy[JsonObjectEncoder[H]]
  ): JsonEncoder[A] =
    createObjectEncoder { value =>
      hEncoder.value.encode(generic.to(value))
    }

  JsonEncoder[IceCream].encode(iceCream)

  trait Penultimate[L] {
    type Out
    def apply(l: L): Out
  }

  object Penultimate {
    type Aux[L, O] = Penultimate[L] { type Out = O }

    def apply[L](implicit p: Penultimate[L]): Aux[L, p.Out] = p
  }

  implicit def hlistPenultimate[L <: HList, M <: HList, O](
      implicit
      init: hlist.Init.Aux[L, M],
      last: hlist.Last.Aux[M, O]
  ): Penultimate.Aux[L, O] =
    new Penultimate[L] {
      type Out = O
      def apply(l: L): O =
        last.apply(init.apply(l))
    }

  implicit def genericPenultimate[A, R, O](
      implicit
      generic: Generic.Aux[A, R],
      penultimate: Penultimate.Aux[R, O]
  ): Penultimate.Aux[A, O] =
    new Penultimate[A] {
      type Out = O
      def apply(a: A): O =
        penultimate.apply(generic.to(a))
    }

  type BigList = String :: Int :: Boolean :: Double :: HNil

  val bigList: BigList = "foo" :: 123 :: true :: 456.0 :: HNil

  implicit class PenultimateOps[A](a: A) {
    def penultimate(implicit inst: Penultimate[A]): inst.Out =
      inst.apply(a)
  }

  bigList.penultimate
}
