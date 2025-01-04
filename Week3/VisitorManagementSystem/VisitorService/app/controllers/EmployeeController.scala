package controllers

import play.api.mvc._
import services.EmployeeService
import play.api.libs.json._
import requests.Employee
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeController @Inject()(
                                    override  val controllerComponents: ControllerComponents,
                                    employeeService: EmployeeService
                                  )(implicit ec: ExecutionContext) extends AbstractController(controllerComponents) {

  /**
   * Add a new employee to the system.
   */
  def addEmployee(): Action[JsValue] = Action.async(parse.json) { request =>
    val employeeJson = request.body
    employeeJson.validate[Employee] match {
      case JsSuccess(employeeData, _) =>
        employeeService.create(employeeData).map { createdEmployee =>
          Created(Json.toJson(createdEmployee))
        }

      case JsError(errors) =>
        val errorResponse = Json.obj(
          "message" -> "Invalid Employee data",
          "errors" -> JsError.toJson(errors)
        )
        Future.successful(BadRequest(errorResponse))
    }
  }

  /**
   * List all employees in the system.
   */
  def list(): Action[AnyContent] = Action.async {
    employeeService.list().map { employeeList =>
      Ok(Json.toJson(employeeList))
    }
  }

  /**
   * Get details of a specific employee by ID.
   */
  def getEmployeeDetails(EmployeeId: Int): Action[AnyContent] = Action.async {
    employeeService.getEmployeeById(EmployeeId).map {
      case Some(employeeDetails) => Ok(Json.toJson(employeeDetails))
      case None =>
        val notFoundResponse = Json.obj("message" -> s"Employee with id $EmployeeId not found")
        NotFound(notFoundResponse)
    }
  }

  /**
   * Delete an employee by ID.
   */
  def deleteEmployee(employeeId: Int): Action[AnyContent] = Action.async {
    employeeService.deleteEmployee(employeeId).map {
      case true => Ok(Json.obj("message" -> s"Employee with ID $employeeId deleted successfully."))
      case false => NotFound(Json.obj("message" -> s"Employee with ID $employeeId not found."))
    }
  }

  /**
   * Update an existing employee's details.
   */
  def updateEmployee(employeeId: Int): Action[JsValue] = Action.async(parse.json) { request =>
    val employeeJson = request.body
    employeeJson.validate[Employee] match {
      case JsSuccess(employeeData, _) =>
        employeeService.updateEmployee(employeeId, employeeData).map {
          case Some(updatedEmployee) => Ok(Json.toJson(updatedEmployee))
          case None =>
            val notFoundResponse = Json.obj("message" -> s"Employee with id $employeeId not found")
            NotFound(notFoundResponse)
        }

      case JsError(errors) =>
        val errorResponse = Json.obj(
          "message" -> "Invalid Employee data",
          "errors" -> JsError.toJson(errors)
        )
        Future.successful(BadRequest(errorResponse))
    }
  }
}
