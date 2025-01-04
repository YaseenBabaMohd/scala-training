package controllers

import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import services.{EmployeeService, KafkaProducerService, VisitorLogService, VisitorService}
import play.api.libs.json._
import requests.{KafkaMessage, Visitor, VisitorIdentityProof, VisitorLog}
import utils.Validation

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VisitorController @Inject()(
                                   val cc: ControllerComponents,
                                   visitorService: VisitorService,
                                   employeeService: EmployeeService,
                                   visitorLogService: VisitorLogService,
                                   kafkaProducerService: KafkaProducerService
                                 )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
   * Handles visitor check-in process with identity proof.
   */
  def checkInVisitor(): Action[MultipartFormData[TemporaryFile]] = Action.async(parse.multipartFormData) { request =>
    val dataParts = request.body.dataParts

    // Extract required fields
    val name = dataParts.get("name").flatMap(_.headOption).getOrElse("")
    val hostName = dataParts.get("hostName").flatMap(_.headOption).getOrElse("")
    val hostMail = dataParts.get("hostMail").flatMap(_.headOption).getOrElse("")
    val email = dataParts.get("email").flatMap(_.headOption).getOrElse("")
    val contactNumber = dataParts.get("contactNumber").flatMap(_.headOption).getOrElse("")

    // Validate required fields
    if (name.isEmpty || hostName.isEmpty || hostMail.isEmpty || email.isEmpty || contactNumber.isEmpty) {
      Future.successful(BadRequest(Json.obj("message" -> "Missing required fields")))
    } else {
      validateAndProcessVisitor(request, name, hostName, hostMail, email, contactNumber)
    }
  }

  /**
   * Validates email and contact number, processes identity proof, and handles check-in.
   */
  private def validateAndProcessVisitor(
                                         request: Request[MultipartFormData[TemporaryFile]],
                                         name: String,
                                         hostName: String,
                                         hostMail: String,
                                         email: String,
                                         contactNumber: String
                                       ): Future[Result] = {
    Validation.validateEmail(email) match {
      case Some(emailError) =>
        Future.successful(BadRequest(Json.obj("message" -> emailError.message)))
      case None =>
        Validation.validateContactNumber(contactNumber) match {
          case Some(contactError) =>
            Future.successful(BadRequest(Json.obj("message" -> contactError.message)))
          case None =>
            processIdentityProof(request, name, hostName, hostMail, email, contactNumber)
        }
    }
  }

  /**
   * Processes identity proof file and handles visitor and host interaction.
   */
  private def processIdentityProof(
                                    request: Request[MultipartFormData[TemporaryFile]],
                                    name: String,
                                    hostName: String,
                                    hostMail: String,
                                    email: String,
                                    contactNumber: String
                                  ): Future[Result] = {
    request.body.file("identityProof") match {
      case Some(filePart) if filePart.filename.toLowerCase.endsWith(".png") =>
        val fileBytes = java.nio.file.Files.readAllBytes(filePart.ref.path)
        handleVisitorCheckIn(name, hostName, hostMail, email, contactNumber, fileBytes)

      case Some(_) =>
        Future.successful(BadRequest(Json.obj("message" -> "Only .png files are accepted as identity proof")))
      case None =>
        Future.successful(BadRequest(Json.obj("message" -> "Missing identity proof file")))
    }
  }

  /**
   * Handles visitor check-in and creates required records.
   */
  private def handleVisitorCheckIn(
                                    name: String,
                                    hostName: String,
                                    hostMail: String,
                                    email: String,
                                    contactNumber: String,
                                    identityProof: Array[Byte]
                                  ): Future[Result] = {
    employeeService.getEmployeeByEmail(hostMail).flatMap {
      case Some(hostEmployee) =>
        visitorService.getVisitorByEmail(email).flatMap {
          case Some(existingVisitor) =>
            logVisitorCheckIn(existingVisitor.visitorId.get, hostEmployee.employeeId.getOrElse(0), name, hostName)

          case None =>
            createNewVisitor(name, email, contactNumber, identityProof, hostEmployee.employeeId.getOrElse(0), hostName)
        }
      case None =>
        Future.successful(BadRequest(Json.obj("message" -> "Host not found in the system")))
    }
  }

  /**
   * Logs an existing visitor's check-in.
   */
  private def logVisitorCheckIn(visitorId: Int, employeeId: Int, visitorName: String, hostName: String): Future[Result] = {
    val visitorLog = VisitorLog(
      visitorId = visitorId,
      employeeId = employeeId,
      checkInTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
      status = "pending"
    )
    visitorLogService.addVisitorLog(visitorLog).map { _ =>
      Ok(Json.toJson(s"Check-in logged successfully for $visitorName. Waiting for $hostName confirmation."))
    }
  }

  /**
   * Creates a new visitor, stores identity proof, and logs the check-in.
   */
  private def createNewVisitor(
                                name: String,
                                email: String,
                                contactNumber: String,
                                identityProof: Array[Byte],
                                employeeId: Int,
                                hostName: String
                              ): Future[Result] = {
    val newVisitor = Visitor(name = name, email = email, contactNumber = contactNumber)

    visitorService.checkIn(newVisitor).flatMap { createdVisitorId =>
      val visitorIdentity = VisitorIdentityProof(visitorId = createdVisitorId, identityProof = identityProof)
      val visitorLog = VisitorLog(
        visitorId = createdVisitorId,
        employeeId = employeeId,
        checkInTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
        status = "pending"
      )
      for {
        _ <- visitorService.addVisitorIdentity(visitorIdentity)
        _ <- visitorLogService.addVisitorLog(visitorLog)
      } yield {
        Ok(Json.toJson(s"Check-in and identity proof added successfully for $name. Waiting for $hostName confirmation."))
      }
    }
  }

  /**
   * Approves a visitor check-in.
   */
  def approveVisitor(visitorId: Int): Action[AnyContent] = Action.async {
    visitorLogService.updateVisitorLogStatus(visitorId, "checked-in").map {
      case Some(_) => Ok("Visitor approved successfully.")
      case None => InternalServerError("Failed to approve the visitor.")
    }
  }

  /**
   * Rejects a visitor check-in.
   */
  def rejectVisitor(visitorId: Int): Action[AnyContent] = Action.async {
    visitorLogService.updateVisitorLogStatus(visitorId, "rejected").map {
      case Some(_) => Ok("Visitor rejected successfully.")
      case None => InternalServerError("Failed to reject the visitor.")
    }
  }

  /**
   * Handles visitor check-out.
   */
  def checkOutVisitor(visitorId: Int): Action[AnyContent] = Action.async {
    visitorLogService.updateCheckOut(visitorId).map {
      case Some(_) => Ok("Visitor checked out successfully.")
      case None => InternalServerError("Failed to check out the visitor.")
    }
  }

  /**
   * Lists all visitors.
   */
  def list(): Action[AnyContent] = Action.async {
    visitorService.list().map { visitors =>
      Ok(Json.toJson(visitors))
    }
  }

  /**
   * Retrieves details of a specific visitor.
   */
  def getVisitorDetails(visitorId: Int): Action[AnyContent] = Action.async {
    visitorService.getVisitorById(visitorId).map {
      case Some(visitor) => Ok(Json.toJson(visitor))
      case None => NotFound(Json.obj("message" -> s"Visitor with id $visitorId not found"))
    }
  }
}
