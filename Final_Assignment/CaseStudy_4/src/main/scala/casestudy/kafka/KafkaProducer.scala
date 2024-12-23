package casestudy.kafka

package KafkaProducer

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import io.circe.syntax._ // For `.asJson` extension
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

// Case class representing the SalesRecord
case class SalesRecord(
                        store: String,         // Store identifier
                        dept: String,          // Department identifier
                        date: String,          // Date of the sales record
                        weeklySales: Float,    // Weekly sales amount
                        isHoliday: Boolean     // Whether it's a holiday
                      )

object SalesProducer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("KafkaSalesProducer")

    // Kafka configuration
    val bootstrapServers = "localhost:9092"
    val topic = "sales-report"

    // Producer settings
    val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

    // Function to create a SalesRecord message
    def createSalesRecord(store: String, dept: String, date: String, weeklySales: Float, isHoliday: Boolean): SalesRecord = {
      SalesRecord(store = store, dept = dept, date = date, weeklySales = weeklySales, isHoliday = isHoliday)
    }

    // Function to generate a Kafka producer record
    def buildRecord(): ProducerRecord[String, String] = {
      val store = s"${Random.nextInt(10) + 1}"  // Random store
      val dept = s"${Random.nextInt(20) + 1}"   // Random department
      val date = java.time.LocalDate.now().toString  // Current date
      val weeklySales = 1000 + Random.nextFloat() * 1000
      val isHoliday = Random.nextBoolean()           // Random holiday flag

      val salesRecord = createSalesRecord(store, dept, date, weeklySales, isHoliday)
      val salesRecordJson = salesRecord.asJson.noSpaces
      println(s"SalesRecord: $salesRecord") // Debugging
      new ProducerRecord[String, String](topic, salesRecordJson)
    }

    // Generate and stream sales records to Kafka
    val salesRecords = Source.tick(0.seconds, 2.seconds, ())
      .map { _ => buildRecord() }

    // Run the Kafka producer
    salesRecords
      .runWith(Producer.plainSink(producerSettings))
      .onComplete { result =>
        println(s"Kafka Producer completed with result: $result")
        system.terminate()
      }
  }
}
