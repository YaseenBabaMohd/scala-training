import model.KafkaMessage
import akka.actor.Actor

/**
 * Visitor IT Support actor for handling visitor status changes and sending corresponding emails (Wi-Fi access, check-out, rejection).
 */
class VisitorITSupportActor extends Actor {
  override def receive: Receive = {
    case msg: KafkaMessage =>
      // Extract relevant information from the Kafka message
      val visitorName = msg.visitorName
      val visitorEmail = msg.visitorMail

      /** Handle different visitor statuses and send corresponding emails */
      msg.visitorStatus match {
        /** When the visitor has checked in */
        case "checked-in" =>
          // Compose Wi-Fi access email content for checked-in visitors at the IT Park
          val subject = s"Welcome to Our IT Park, $visitorName! Wi-Fi Access Details"
          val body =
            s"""
               |Hello $visitorName,
               |
               |Welcome to our IT Park! We're excited to have you here.
               |
               |To ensure you stay connected, here are your Wi-Fi access details:
               |
               |Wi-Fi Network: WAVEROCK
               |Password: W4v3R0ck!
               |
               |If you need any IT assistance or have questions about the facilities, please don't hesitate to reach out to our IT Support team.
               |
               |Enjoy your time at the IT Park, and feel free to explore the innovative environment around you!
               |
               |Best regards,
               |IT Support Team, WaveRock IT Park
               |""".stripMargin

          // Send Wi-Fi details email
          sendEmail(visitorEmail, subject, body)

        /** When the visitor has checked out */
        case "checked-out" =>
          // Compose exit confirmation email content for checked-out visitors at the IT Park
          val subject = s"Thank You for Visiting WaveRock IT Park, $visitorName! Your Check-Out is Complete"
          val body =
            s"""
               |Dear $visitorName,
               |
               |Thank you for visiting WaveRock IT Park! We hope you had a productive visit and enjoyed the facilities.
               |
               |This is a confirmation that your check-out has been successfully completed.
               |
               |We hope to welcome you again in the future. If you need any further assistance, feel free to reach out to our team.
               |
               |Safe travels, and best of luck with your endeavors!
               |
               |Best regards,
               |IT Support Team, WaveRock IT Park
               |""".stripMargin

          // Send the exit confirmation email
          sendEmail(visitorEmail, subject, body)

        /** When the visitor's entry request is rejected */
        case "rejected" =>
          // Compose rejection notification email content for rejected visitors at the IT Park
          val subject = s"Entry Request Rejected for $visitorName at WaveRock IT Park"
          val body =
            s"""
               |Dear $visitorName,
               |
               |We regret to inform you that your entry request for WaveRock IT Park has been declined.
               |
               |If you believe this is an error or require more details, please contact our support team, and we will assist you further.
               |
               |We appreciate your understanding, and we hope to resolve this matter promptly.
               |
               |Best regards,
               |Security Team, WaveRock IT Park
               |""".stripMargin

          // Send the rejection notification email
          sendEmail(visitorEmail, subject, body)

        /** Handle any unknown visitor status */
        case _ =>
          println(s"Unknown visitor status: ${msg.visitorStatus}")
      }
  }

  /** Helper method to send an email to the visitor */
  private def sendEmail(to: String, subject: String, body: String): Unit = {
    EmailUtils.sendEmail(to, subject, body)
    println(s"Email sent to $to successfully.")
  }
}

/** Host Processor actor for handling visitor arrival, check-in, check-out, and rejection at the IT Park */
class VisitorHostActor extends Actor {
  override def receive: Receive = {
    case msg: KafkaMessage =>
      // Extract relevant details from the Kafka message
      val visitorName = msg.visitorName
      val hostName = msg.employeeName
      val visitorContactNumber = msg.visitorContactNumber
      val hostEmail = msg.employeeMail

      /** Handle different visitor statuses and send appropriate emails to the host */
      msg.visitorStatus match {
        /** When the visitor is pending (awaiting host approval) */
        case "pending" =>
          // Compose arrival notification email for the host to approve or reject the visitor
          val subject = s"Action Required: Approve Entry for Visitor $visitorName at WaveRock IT Park"
          val approvalLink = s"http://localhost:9000/visitor/approve/${msg.visitorId}"
          val rejectionLink = s"http://localhost:9000/visitor/reject/${msg.visitorId}"
          val body =
            s"""
               |Dear $hostName,
               |
               |This is a notification that your visitor, $visitorName, has arrived at WaveRock IT Park and is awaiting your approval to enter.
               |
               |Visitor's Contact: $visitorContactNumber
               |
               |To approve entry, please click here: $approvalLink
               |To reject entry, click here: $rejectionLink
               |
               |Best regards,
               |Visitor Management Team, WaveRock IT Park
               |""".stripMargin

          // Send the arrival notification email to the host
          sendEmailToHost(hostEmail, subject, body)

        /** When the visitor has checked in */
        case "checked-in" =>
          // Compose check-in confirmation email to the host
          val subject = s"Visitor $visitorName Has Checked-in at WaveRock IT Park"
          val body =
            s"""
               |Dear $hostName,
               |
               |This is a confirmation that your visitor, $visitorName, has successfully checked in at WaveRock IT Park.
               |
               |We hope your visitor has a productive visit. If you need any further assistance, feel free to reach out to our team.
               |
               |Best regards,
               |Visitor Management Team, WaveRock IT Park
               |""".stripMargin

          // Send the check-in confirmation email to the host
          sendEmailToHost(hostEmail, subject, body)

        /** When the visitor has checked out */
        case "checked-out" =>
          // Compose check-out notification email to the host
          val subject = s"Visitor $visitorName Has Checked Out from WaveRock IT Park"
          val body =
            s"""
               |Dear $hostName,
               |
               |This is a notification that your visitor, $visitorName, has checked out from WaveRock IT Park.
               |
               |We hope the visit was successful and we look forward to hosting your visitors in the future.
               |
               |Best regards,
               |Visitor Management Team, WaveRock IT Park
               |""".stripMargin

          // Send the check-out notification email to the host
          sendEmailToHost(hostEmail, subject, body)

        /** When the visitor's entry request is rejected */
        case "rejected" =>
          // Compose rejection confirmation email for the host
          val subject = s"Visitor $visitorName's Entry Request Rejected at WaveRock IT Park"
          val body =
            s"""
               |Dear $hostName,
               |
               |This is a confirmation that the entry request for your visitor, $visitorName, has been rejected at WaveRock IT Park.
               |
               |If you need further assistance, please feel free to contact our Visitor Management team.
               |
               |Best regards,
               |Visitor Management Team, WaveRock IT Park
               |""".stripMargin

          // Send the rejection confirmation email to the host
          sendEmailToHost(hostEmail, subject, body)

        /** Handle any unknown visitor status */
        case _ =>
          println(s"Unknown visitor status: ${msg.visitorStatus}")
      }
  }

  /** Helper method to send an email to the host */
  private def sendEmailToHost(to: String, subject: String, body: String): Unit = {
    EmailUtils.sendEmail(to, subject, body)
    println(s"Email sent to host at $to successfully.")
  }
}

/** Security Processor actor for notifying the security team about visitor status changes (check-in, check-out, pending, rejected). */
class VisitorSecurityActor extends Actor {
  /** The 'receive' method listens for messages related to visitor status changes. */
  override def receive: Receive = {
    case msg: KafkaMessage =>
      /** Match on the visitor status to send the appropriate message to the security team. */
      msg.visitorStatus match {

        /** When the visitor has checked in, notify the security team. */
        case "checked-in" =>
          println(s"Security Team Notification: Visitor ${msg.visitorName} has successfully checked in at WaveRock IT Park.")
          println("Ensure the visitor is granted access to the premises and notify the concerned host if necessary.")

        /** When the visitor has checked out, notify the security team. */
        case "checked-out" =>
          println(s"Security Team Notification: Visitor ${msg.visitorName} has checked out from WaveRock IT Park.")
          println("Ensure the visitor’s exit is noted, and the premises are secured.")

        /** When the visitor status is pending (awaiting approval), notify the security team that action is needed. */
        case "pending" =>
          println(s"Security Team Notification: Visitor ${msg.visitorName} is currently awaiting host approval for entry at WaveRock IT Park.")
          println("Please be alert for the host’s response to allow or deny entry.")

        /** When the visitor's entry is rejected, notify the security team. */
        case "rejected" =>
          println(s"Security Team Notification: Visitor ${msg.visitorName} has been rejected entry at WaveRock IT Park.")
          println("Please ensure the visitor is politely informed, and take appropriate security measures.")

        /** Handle unexpected or unknown visitor statuses. */
        case _ =>
          println(s"Security Team Alert: Unknown visitor status received for ${msg.visitorName}. Status: ${msg.visitorStatus}")
          println("Please review the visitor entry logs and take action as necessary.")
      }
  }
}
