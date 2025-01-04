package services

import play.api.libs.json._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import requests.KafkaMessage
import java.util.Properties
import javax.inject._

@Singleton
class KafkaProducerService @Inject()() {

  /** Kafka configuration properties */
  private val kafkaBootstrapServers = "localhost:9092"
  private val kafkaKeySerializer = "org.apache.kafka.common.serialization.StringSerializer"
  private val kafkaValueSerializer = "org.apache.kafka.common.serialization.StringSerializer"
  private val kafkaTopic = "waverock-visitor"

  /** Initialize Kafka producer properties */
  private val props = new Properties()
  props.put("bootstrap.servers", kafkaBootstrapServers)
  props.put("key.serializer", kafkaKeySerializer)
  props.put("value.serializer", kafkaValueSerializer)

  // Create Kafka producer instance
  private val producer = new KafkaProducer[String, String](props)

  /**
   * Sends a Kafka message to the configured topic.
   * @param kafkaRequest The KafkaMessage object to send.
   */
  def sendToKafka(kafkaRequest: KafkaMessage): Unit = {
    /** Serialize the KafkaMessage object to JSON */
    val jsonMessage: String = Json.stringify(Json.toJson(kafkaRequest))

    /** Create a producer record with the message and send it to Kafka */
    val record = new ProducerRecord[String, String](kafkaTopic, "key", jsonMessage)
    producer.send(record)
  }
}
