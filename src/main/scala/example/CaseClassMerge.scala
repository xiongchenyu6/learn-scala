package example

import shapeless._

object CaseClassMerge extends App {

  case class Apple(name : String, price : Double, quantity : Int)

  val gen = LabelledGeneric[Apple]

  val t = Apple("aaa", 12.20, 2)

  val m = gen.to(t)

}
