package models

import requests.Employee
import slick.jdbc.MySQLProfile.api._

class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employee") {
  def employeeId = column[Option[Int]]("employee_id", O.PrimaryKey, O.AutoInc)
  def employeeName = column[String]("employee_name")
  def organisation = column[String]("organisation")
  def building = column[String]("building")
  def email = column[String]("email")
  def contactNo = column[String]("contact_no")

  def * = (employeeId, employeeName, organisation, building, email, contactNo) <> ((Employee.apply _).tupled, Employee.unapply)
}
