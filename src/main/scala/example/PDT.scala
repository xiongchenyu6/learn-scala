package example

object PDT extends App {
  val a1 = new AT
  val a2 = new AT
//  a2.f(new a1.B) won't compile
  a2.g(new a1.B)
}
