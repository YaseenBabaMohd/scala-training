import akka.actor.{Actor, ActorRef}
import models.KafkaMessageFormat
import org.slf4j.LoggerFactory

class CorporateEqpAllocationFileWriterActor() extends Actor {
  def receive: Receive = {
    case (fileName: String, messageType: String, message: String) =>
      EmailUtils.sendEmail("yaseenbaba12345@gmail.com", messageType, message)
  }
}


class ManagerApprovalMessageListener(fileWriterActor: ActorRef) extends Actor {


  override def receive: Receive = {
    case msg: KafkaMessageFormat =>
      println("Manager Approval Message Listener consumes the message")
  }
}

class InventoryMessageListener(fileWriterActor: ActorRef) extends Actor {
  private val logger = LoggerFactory.getLogger("InventoryMessageLogger")

  override def receive: Receive = {
    case msg: KafkaMessageFormat =>
      println("Inventory Message Listener consumes the message")
      EmailUtils.sendEmail("yaseenbaba12345@gmail.com", msg.messageType, msg.message)

  }
}

class MaintenanceMessageListener(fileWriterActor: ActorRef) extends Actor {

  override def receive: Receive = {
    case msg: KafkaMessageFormat =>
      println("Maintenance Message Listener consumes the message")
      EmailUtils.sendEmail("yaseenbaba12345@gmail.com", msg.messageType, msg.message)
  }
}

class EmployeeMessageListener(fileWriterActor: ActorRef) extends Actor {
  private val logger = LoggerFactory.getLogger("EmployeeMessageLogger")

  override def receive: Receive = {
    case msg: KafkaMessageFormat =>
      println("Employee Message Listener consumes the message")
      EmailUtils.sendEmail(msg.receiver, msg.messageType, msg.message)
  }
}

class CorporateEquipAllocation(managerApprovalMessageListener: ActorRef,
                               inventoryMessageListener: ActorRef,
                               maintenanceMessageListener: ActorRef,
                               employeeMessageListener: ActorRef
                              )extends Actor {
  override def receive: Receive = {
    case msg: KafkaMessageFormat => msg.receiver match {
      case "MANAGER" =>
        managerApprovalMessageListener ! msg
      case "INVENTORY" =>
        inventoryMessageListener ! msg
      case "MAINTENANCE" =>
        maintenanceMessageListener ! msg
      case "EMPLOYEE" =>
        employeeMessageListener ! msg
    }
  }

}
