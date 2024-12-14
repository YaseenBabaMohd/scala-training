import com.google.gson.{Gson, JsonParser}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.io.Source
import java.util.Properties
import scala.collection.convert.ImplicitConversions.`iterator asScala`

object KafkaProducer {
  def main(args: Array[String]): Unit = {
    val topic = "transactions"
    val source = "/Users/yaseenbabamohammad/Desktop/Scala_DE_Training/CaseStudy3/transactions.csv"

    val properties = new Properties()
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")


    val producer = new KafkaProducer[String, String](properties)


    try {
      val messages = readJsonFile(source)
      messages.foreach{msg =>
        producer.send(new ProducerRecord[String, String](topic, msg))
        Thread.sleep(1000)
      }
    }
    finally{
      producer.close()
    }

    def readJsonFile(filePath: String): List[String] = {
      try {
        val content = Source.fromFile(filePath).mkString
        val gson = new Gson()
        val jsonElement = JsonParser.parseString(content)

        if (jsonElement.isJsonArray) {
          jsonElement.getAsJsonArray.iterator().map(x=>gson.toJson(x)).toList
        } else {
          List(jsonElement.toString)
        }
      } catch {
        case e: Exception =>
          println(s"Error reading file: ${e.getMessage}")
          List.empty
      }
    }


  }
}
