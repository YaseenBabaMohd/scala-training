import org.apache.kafka.common.serialization.StringDeserializer
import akka.actor.{ActorSystem, Props}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import model.KafkaMessage
import spray.json._
import model.JsonFormats.kafkaMessageFormat

object VisitorNotificationConsumerApp extends App {

  // Create an Actor System for managing all actors.
  val system = ActorSystem("VisitorNotificationSystem")
  implicit val materializer: Materializer = ActorMaterializer()(system)

  // Define the topic name as a variable
  val visitorStatusTopic = "waverock-visitor"  // Kafka topic for visitor status updates

  // Instantiate the renamed actors with new names.
  val itSupportProcessorActor = system.actorOf(Props[VisitorITSupportActor], "itSupportProcessorActor")
  val hostProcessorActor = system.actorOf(Props[VisitorHostActor], "hostProcessorActor")
  val securityProcessorActor = system.actorOf(Props[VisitorSecurityActor], "securityProcessorActor")

  // Instantiate NotificationRouter actor that routes messages to the respective processors.
  val notificationRouterActor = system.actorOf(Props(new NotificationHandler(itSupportProcessorActor, hostProcessorActor, securityProcessorActor)), "notificationRouterActor")

  // Define Kafka consumer settings to consume messages from the Kafka topic.
  val kafkaConsumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")  // Kafka broker address
    .withGroupId("visitor-consumer-group")   // Consumer group ID
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")  // Start consuming from the earliest offset
    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")    // Enable auto commit for offset management

  // Create the Kafka consumer source and process incoming messages from the specified topic.
  Consumer.plainSource(kafkaConsumerSettings, Subscriptions.topics(visitorStatusTopic))  // Use the variable for topic name
    .map(record => record.value().parseJson.convertTo[KafkaMessage])  // Deserialize the Kafka message to KafkaMessage type
    .runWith(Sink.foreach(message => notificationRouterActor ! message))   // Send the deserialized message to the NotificationRouter actor

  // Print a message indicating the consumers are active and consuming messages from the topic.
  println(s"Consumers are active and consuming messages from the topic: $visitorStatusTopic")
}
