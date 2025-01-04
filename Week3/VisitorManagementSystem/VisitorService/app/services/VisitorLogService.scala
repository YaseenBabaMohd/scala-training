package services

import com.google.inject.Inject
import repositories.VisitorLogRepository

import javax.inject.Singleton
import requests.{KafkaMessage, VisitorLog}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class VisitorLogService @Inject()(
                                   visitorLogRepository: VisitorLogRepository,
                                   visitorService: VisitorService,
                                   employeeService: EmployeeService,
                                   kafkaProducerService: KafkaProducerService
                                 ) {

  /**
   * Adds a new visitor log entry to the repository.
   * Once the log is persisted, fetches visitor and employee details,
   * constructs a KafkaMessage, and sends it to Kafka.
   *
   * @param visitorLog The visitor log to add.
   * @return A Future containing the ID of the persisted visitor log.
   */
  def addVisitorLog(visitorLog: VisitorLog): Future[Int] = {
    val persistedVisitorLogFuture = visitorLogRepository.addVisitorLog(visitorLog)

    persistedVisitorLogFuture.flatMap { visitorLogId =>
      for {
        visitorOption <- visitorService.getVisitorById(visitorLog.visitorId)
        employeeOption <- employeeService.getEmployeeById(visitorLog.employeeId)
      } yield {
        (visitorOption, employeeOption) match {
          case (Some(visitor), Some(employee)) =>
            // Create a KafkaMessage with the required details
            val kafkaRequest = KafkaMessage(
              visitorId = visitorLogId,
              visitorName = visitor.name,
              employeeName = employee.employeeName,
              visitorMail = visitor.email,
              employeeMail = employee.email,
              visitorContactNumber = visitor.contactNumber,
              visitorStatus = visitorLog.status
            )
            // Send the message to Kafka
            kafkaProducerService.sendToKafka(kafkaRequest)
            visitorLogId

          case _ =>
            // Throw an exception if visitor or employee details are missing
            throw new IllegalStateException(
              s"Unexpected state: Visitor with ID ${visitorLog.visitorId} or Employee with ID ${visitorLog.employeeId} not found."
            )
        }
      }
    }
  }

  /**
   * Updates the status of an existing visitor log entry.
   * Fetches visitor and employee details, constructs a KafkaMessage,
   * and sends it to Kafka after the update.
   *
   * @param visitorId The ID of the visitor log to update.
   * @param newStatus The new status to set.
   * @return A Future containing the updated VisitorLog, or None if not found.
   */
  def updateVisitorLogStatus(visitorId: Int, newStatus: String): Future[Option[VisitorLog]] = {
    visitorLogRepository.updateVisitorLogStatus(visitorId, newStatus).flatMap {
      case Some(visitorLog) =>
        for {
          visitorOption <- visitorService.getVisitorById(visitorLog.visitorId)
          employeeOption <- employeeService.getEmployeeById(visitorLog.employeeId)
        } yield {
          (visitorOption, employeeOption) match {
            case (Some(visitor), Some(employee)) =>
              // Create a KafkaMessage with the updated status
              val kafkaRequest = KafkaMessage(
                visitorId = visitorLog.visitorId,
                visitorName = visitor.name,
                employeeName = employee.employeeName,
                visitorMail = visitor.email,
                employeeMail = employee.email,
                visitorContactNumber = visitor.contactNumber,
                visitorStatus = newStatus
              )
              // Send the message to Kafka
              kafkaProducerService.sendToKafka(kafkaRequest)

            case _ => // Log or handle cases where visitor or employee details are not found
          }
          // Return the updated visitor log entry
          Some(visitorLog)
        }

      case None =>
        // Return None if no visitor log entry is found to update
        Future.successful(None)
    }
  }

  /**
   * Updates the check-out time and status for a visitor log entry.
   * Fetches visitor and employee details, constructs a KafkaMessage,
   * and sends it to Kafka after the update.
   *
   * @param visitorId The ID of the visitor log to update.
   * @return A Future containing the updated VisitorLog, or None if not found.
   */
  def updateCheckOut(visitorId: Int): Future[Option[VisitorLog]] = {
    visitorLogRepository.updateCheckOut(visitorId).flatMap {
      case Some(visitorLog) =>
        for {
          visitorOption <- visitorService.getVisitorById(visitorLog.visitorId)
          employeeOption <- employeeService.getEmployeeById(visitorLog.employeeId)
        } yield {
          (visitorOption, employeeOption) match {
            case (Some(visitor), Some(employee)) =>
              // Create a KafkaMessage with the "checked-out" status
              val kafkaRequest = KafkaMessage(
                visitorId = visitorLog.visitorId,
                visitorName = visitor.name,
                employeeName = employee.employeeName,
                visitorMail = visitor.email,
                employeeMail = employee.email,
                visitorContactNumber = visitor.contactNumber,
                visitorStatus = "checked-out"
              )
              // Send the message to Kafka
              kafkaProducerService.sendToKafka(kafkaRequest)

            case _ => // Log or handle cases where visitor or employee details are not found
          }
          // Return the updated visitor log entry
          Some(visitorLog)
        }

      case None =>
        // Return None if no visitor log entry is found to update
        Future.successful(None)
    }
  }
}
