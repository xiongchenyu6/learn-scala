package example

import scala.quoted.* // Import `quotes`, `Quotes`, and `Expr`

object Phantom extends App {
  trait Status
  trait Open   extends Status
  trait Closed extends Status

  trait Door[S <: Status]

  object Door {
    def apply[S <: Status] = new Door[S] {}

     def opens[S <: Status](d: Door[S]) = {

       if (d.isInstanceOf[Door[Open]]) {

         println("error")
       }
     }
    def open[S <: Closed](d: Door[S]) = Door[Open]
    def close[S <: Open](d: Door[S])  = Door[Closed]
  }

  val closedDoor      = Door[Closed]
  val openDoor        = Door.open(closedDoor)
  val closedAgainDoor = Door.close(openDoor)

  // val closedClosedDoor = Door.close(closedDoor)
   val openOpenDoor = Door.open(closedDoor)

}
