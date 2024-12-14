import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import scala.util.Random

object KafkaMessageProducer {
  def main(args: Array[String]): Unit = {
    val topic = "orders"
    val random = new Random()

    val properties = new Properties()
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")


    val producer = new KafkaProducer[String, String](properties)

    val sendRecord: () => Unit = () => {
      val (orderId, userId, amount) = (
        random.nextInt(1000) + 1,
        100 + random.nextInt(100) + 1,
        BigDecimal(random.nextDouble() * 500).setScale(2, BigDecimal.RoundingMode.HALF_UP)
      )

      val msg = s"""{"order_id": $orderId, "user_id": $userId, "amount": $amount}"""
      val record = new ProducerRecord[String, String](topic, msg)
      producer.send(record)
      println(s"Message: $msg sent at ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
    }

    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    scheduler.scheduleAtFixedRate(() => sendRecord(), 0, 1, TimeUnit.SECONDS)


    sys.addShutdownHook {
      println("Shutting down Kafka producer and scheduler...")
      scheduler.shutdown() // Stop the scheduler
      try {
        if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
          scheduler.shutdownNow() // Force shutdown if tasks don’t terminate in time
        }
      } catch {
        case _: InterruptedException =>
          scheduler.shutdownNow() // Force shutdown on interrupt
      }
      producer.close() // Close Kafka producer
      println("Kafka producer stopped.")
    }
  }

}
