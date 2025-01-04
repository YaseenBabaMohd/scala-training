import model.KafkaMessage
import akka.actor.Actor
import akka.actor.ActorRef

/**
 * NotificationHandler actor class for processing visitor status updates and notifying the appropriate actors
 * (IT Support, Host, and Security) based on the status of the visitor.
 */
class NotificationHandler(
                           itSupportProcessor: ActorRef,   // IT Support processor actor reference
                           hostProcessor: ActorRef,       // Host processor actor reference
                           securityProcessor: ActorRef    // Security processor actor reference
                         ) extends Actor {

  /**
   * The 'receive' method listens for incoming visitor status messages, processes them,
   * and forwards the messages to the relevant actors (IT Support, Host, and Security).
   */
  override def receive: Receive = {
    case msg: KafkaMessage => {
      /**
       * Match on the visitor's status and forward the message to the appropriate processors based on the status.
       */
      msg.visitorStatus match {

        /**
         * When the visitor's status is "pending", notify both the Host and Security actors
         * to handle the visitor's approval for entry.
         */
        case "pending" =>
          hostProcessor ! msg  // Forward the visitor's message to the Host processor for approval
          securityProcessor ! msg  // Notify Security about the pending visitor status

        /**
         * When the visitor's status is "checked-in", "checked-out", or "rejected", notify
         * the IT Support, Host, and Security actors to process the status change.
         */
        case "checked-in" | "checked-out" | "rejected" =>
          itSupportProcessor ! msg  // Forward the message to the IT Support processor for further processing
          hostProcessor ! msg  // Forward the message to the Host processor to inform about the visitor's status
          securityProcessor ! msg  // Notify the Security processor about the visitor's status change

        /**
         * If the visitor's status is unknown, print a message indicating that the status is unrecognized.
         */
        case _ =>
          println(s"Unknown visitor status: ${msg.visitorStatus}")
      }
    }
  }
}
