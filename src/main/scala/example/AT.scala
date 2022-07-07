package example

class AT {
  class B
  def f(b: B)    = println("Got my B!")
  def g(b: AT#B) = println("Got a B.")
}