import java.io.File
import scala.sys.process._

object KafkaAndZookeeperStarter {

  def main(args: Array[String]): Unit = {
    try {
      // Paths to configuration files (adjust these paths)
      val zookeeperConfig = "/Users/yaseenbabamohammad/Desktop/kafkaCluster/config/zookeeper.properties"
      val kafkaConfig = "/Users/yaseenbabamohammad/Desktop/kafkaCluster/config/server.properties"

      // Paths to Kafka binary scripts
      val kafkaHome = "/Users/yaseenbabamohammad/Desktop/kafkaCluster"
      val zookeeperScript = s"$kafkaHome/bin/zookeeper-server-start.sh"
      val kafkaScript = s"$kafkaHome/bin/kafka-server-start.sh"

      // Start ZooKeeper in a separate thread
      val zookeeperThread = new Thread(new Runnable {
        override def run(): Unit = {
          println("Starting ZooKeeper...")
          val process = Process(Seq("sh", zookeeperScript, zookeeperConfig))
          process.!
        }
      })

      // Start Kafka in a separate thread
      val kafkaThread = new Thread(new Runnable {
        override def run(): Unit = {
          println("Starting Kafka...")
          val process = Process(Seq("sh", kafkaScript, kafkaConfig))
          process.!
        }
      })

      // Start both servers
      zookeeperThread.start()
      Thread.sleep(5000) // Wait for ZooKeeper to initialize
      kafkaThread.start()

      println("ZooKeeper and Kafka servers are starting...")

      // Join threads (optional, keeps the main program running)
      zookeeperThread.join()
      kafkaThread.join()

    } catch {
      case e: Exception =>
        println(s"Error while starting ZooKeeper or Kafka: ${e.getMessage}")
    }
  }
}
