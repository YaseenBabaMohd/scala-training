case class Task(description: String, durationInSeconds: Int)

object Task {
  def apply(description: String, durationInSeconds: Int)(executeLogic: => Unit): Task = {
    println(s"Job will start after waiting for $durationInSeconds seconds")
    Thread.sleep(durationInSeconds * 1000)
    executeLogic
    new Task(description, durationInSeconds)
  }
}

@main def main: Unit = {
  val delayTime: Int = 5
  val task = Task("Task after delay", delayTime) {
    println(s"Executed logic after waiting for $delayTime seconds!")
  }
}
