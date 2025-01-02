//trait Preparation {
//  def setUp(): Unit = {
//    println("Setting up the pitch and team.")
//  }
//}
//
//trait Batting extends Preparation {
//  override def setUp(): Unit = {
//    super.setUp()
//    println("Batting team is ready to take the field.")
//  }
//}
//
//trait Bowling {
//  def startBowling(): Unit = {
//    println("Bowler is running in to bowl the ball.")
//  }
//}
//
//class Match extends Batting with Bowling {
//  def startMatch(): Unit = {
//    setUp()
//    startBowling()
//  }
//}
//
//@main def main: Unit = {
//  val cricketMatch = new Match()
//  cricketMatch.startMatch()
//}
