import akka.actor.{ActorRef, ActorSystem, Props}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._
import models.JsonFormats.kafkaMessageFormat
import models.KafkaMessageFormat

object MainApp {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("MessagingConsumerSystem")

    implicit val materializer: Materializer = ActorMaterializer()(system)

    val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    // Define file writer actors
    val ceaFileWriterActor: ActorRef = system.actorOf(Props[CorporateEqpAllocationFileWriterActor], "CorporateEqpAllocationFileWriterActor")


    // Configure Corporate Equipment Allocation Listeners
    val corporateEquipAllocationListener = system.actorOf(Props(new CorporateEquipAllocation(
      system.actorOf(Props(new ManagerApprovalMessageListener(ceaFileWriterActor))),
      system.actorOf(Props(new InventoryMessageListener(ceaFileWriterActor))),
      system.actorOf(Props(new MaintenanceMessageListener(ceaFileWriterActor))),
      system.actorOf(Props(new EmployeeMessageListener(ceaFileWriterActor)))
    )))

    startListener(consumerSettings,"corporate-equipment-allocation-topic", corporateEquipAllocationListener)

  }
  private def startListener(consumerSettings: ConsumerSettings[String, String], topic: String, listener: ActorRef)
                           (implicit materializer: Materializer): Unit = {
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topic))
      .map(record => record.value().parseJson.convertTo[KafkaMessageFormat])
      .runWith(Sink.actorRef[KafkaMessageFormat](listener, onCompleteMessage = "complete", onFailureMessage = (throwable: Throwable) => s"Exception encountered"))
  }


}

