import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Random

object AsyncAwait {

  def main(args: Array[String]): Unit = {

    def performAsyncTask(): Future[String] = {
      val taskCompleted = new AtomicBoolean(false)
      val promise = Promise[String]()

      def createAndStartWorker(workerName: String): Thread = new Thread(new Runnable {
        def run(): Unit = {
          val random = new Random()
          while (!taskCompleted.get() && !promise.isCompleted) {
            val randomValue = random.nextInt(3000) // Change range of random number generation
            if (randomValue == 2222 && !taskCompleted.getAndSet(true)) {
              promise.success(s"$workerName has generated the target value 2222")
            }
          }
        }
      })

      val workers = List("Worker A", "Worker B", "Worker C").map(createAndStartWorker)
      workers.foreach(_.start())

      promise.future
    }

    val futureTaskResult: Future[String] = performAsyncTask()

    futureTaskResult.onComplete {
      case Success(message) => println(message)
      case Failure(exception) => println(s"An error occurred: ${exception.getMessage}")
    }

    // Wait to allow threads to finish their execution
    Thread.sleep(10000)
  }
}
