//trait GameTask {
//  def executeTask(): Unit = {
//    println("Setting up for the game.")
//  }
//}
//
//trait Batting extends GameTask {
//  override def executeTask(): Unit = {
//    println("Preparing for batting.")
//  }
//}
//
//trait Bowling extends Batting {
//  override def executeTask(): Unit = {
//    println("Bowling team is ready.")
//  }
//}
//
//trait Fielding extends Bowling {
//  override def executeTask(): Unit = {
//    println("Fielding team is getting in position.")
//  }
//}
//
//class MatchPreparation extends GameTask {
//  def startMatchPreparation(): Unit = {
//    println("Starting match preparation...")
//    executeTask()
//  }
//}
//
//@main def main: Unit = {
//  val matchPrep = new MatchPreparation()
//  matchPrep.startMatchPreparation()
//  println("---")
//  val dynamicMatchPrep: GameTask = new MatchPreparation with Batting with Fielding with Bowling
//  dynamicMatchPrep.executeTask()
//}
