package services

import repositories.EmployeeRepository
import requests.Employee

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeService @Inject()(employeeRepository: EmployeeRepository)(implicit ec: ExecutionContext) {

  /**
   * Creates a new employee record in the repository.
   * @param employeeData The employee data to create.
   * @return A Future containing the created Employee object.
   */
  def create(employeeData: Employee): Future[Employee] =
    employeeRepository.create(employeeData)

  /**
   * Validates if the provided email exists for any employee.
   * @param email The email to validate.
   * @return A Future containing true if valid, otherwise false.
   */
  def isEmployeeEmailValid(email: String): Future[Boolean] =
    employeeRepository.isEmployeeEmailValid(email)

  /**
   * Retrieves a list of all employees from the repository.
   * @return A Future containing a sequence of Employee objects.
   */
  def list(): Future[Seq[Employee]] =
    employeeRepository.list()

  /**
   * Fetches details of an employee by their ID.
   * @param employeeId The ID of the employee.
   * @return A Future containing an Option of Employee if found, otherwise None.
   */
  def getEmployeeById(employeeId: Int): Future[Option[Employee]] =
    employeeRepository.getEmployeeById(employeeId)

  /**
   * Deletes an employee by their ID.
   * @param employeeId The ID of the employee to delete.
   * @return A Future containing true if deletion was successful, otherwise false.
   */
  def deleteEmployee(employeeId: Int): Future[Boolean] =
    employeeRepository.deleteEmployee(employeeId)

  /**
   * Fetches an employee's details by their email address.
   * @param email The email of the employee.
   * @return A Future containing an Option of Employee if found, otherwise None.
   */
  def getEmployeeByEmail(email: String): Future[Option[Employee]] =
    employeeRepository.getEmployeeByMail(email)

  /**
   * Updates an employee's details by their ID.
   * @param employeeId The ID of the employee to update.
   * @param updatedEmployeeData The updated employee data.
   * @return A Future containing an Option of the updated Employee if successful, otherwise None.
   */
  def updateEmployee(employeeId: Int, updatedEmployeeData: Employee): Future[Option[Employee]] = {
    getEmployeeById(employeeId).flatMap {
      case Some(_) =>
        employeeRepository.updateEmployee(employeeId, updatedEmployeeData).flatMap {
          case rowsAffected if rowsAffected > 0 =>
            getEmployeeById(employeeId) // Fetch updated employee data
          case _ =>
            Future.successful(None)
        }
      case None =>
        Future.successful(None) // Employee not found
    }
  }

}
