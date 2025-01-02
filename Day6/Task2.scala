case class Employee(sno: Int, name: String, city: String, department: String)

class Department(val departmentName: String, var employees: List[Employee], var subDepartments: List[Department]) {

  def addEmployee(employee: Employee): Unit = {
    if (employee.department == departmentName) {
      employees = employees :+ employee
    } else {
      subDepartments.foreach(_.addEmployee(employee))
    }
  }

  def addSubDepartment(subDepartment: Department): Unit = {
    subDepartments = subDepartments :+ subDepartment
  }

  def printDepartment(indent: String = "", isLast: Boolean = true): Unit = {
    val line = if (isLast) "└──" else "├──"
    println(s"$indent$line $departmentName")
    employees.foreach { emp =>
      println(s"$indent    ├── (ID: ${emp.sno}, Name: ${emp.name}, City: ${emp.city})")
    }
    subDepartments.zipWithIndex.foreach { case (subDept, index) =>
      subDept.printDepartment(indent + (if (isLast) "    " else "│   "), index == subDepartments.length - 1)
    }
  }
}

object OrganizationApp {

  def createDepartment(departmentName: String): Department = {
    new Department(departmentName, Nil, Nil)
  }

  def main(args: Array[String]): Unit = {
    val finance = createDepartment("Finance")
    val sales = createDepartment("Sales")
    val marketing = createDepartment("Marketing")
    val hr = createDepartment("HR")
    val tech = createDepartment("Tech")

    finance.addSubDepartment(sales)
    sales.addSubDepartment(marketing)
    finance.addSubDepartment(hr)
    finance.addSubDepartment(tech)

    println("Welcome to the Organization Tree Application!")
    var continue = true

    while (continue) {
      println("\nEnter employee details (sno, name, city, department), or type 'exit' to stop.")
      val input = scala.io.StdIn.readLine()

      if (input.toLowerCase == "exit") {
        continue = false
      } else {
        val details = input.split(",").map(_.trim)

        if (details.length == 4) {
          try {
            val sno = details(0).toInt
            val name = details(1)
            val city = details(2)
            val department = details(3)
            val employee = Employee(sno, name, city, department)
            finance.addEmployee(employee) // Adds to the root department, which will propagate to sub-departments if necessary

            println(s"Employee added: $name to $department department.")
          } catch {
            case e: Exception => println("Invalid input. Please enter in the format: sno,name,city,department")
          }
        } else {
          println("Invalid input. Please enter exactly 4 comma-separated values: sno,name,city,department.")
        }
      }
    }

    println("\nOrganization Structure:")
    finance.printDepartment()
  }
}
