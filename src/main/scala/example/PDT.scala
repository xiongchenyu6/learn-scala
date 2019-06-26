package example

class AT {
  class B
  def f(b: B)    = println("Got my B!")
  def g(b: AT#B) = println("Got a B.")
}

object PDA extends App {
  val a1 = new AT
  val a2 = new AT
//  a2.f(new a1.B) won't compile
  a2.g(new a1.B)
}
