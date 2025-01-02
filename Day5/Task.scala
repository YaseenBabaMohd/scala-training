import scala.io.Source

// Case class to represent an Employee
case class Worker(id: Int, name: String, city: String, salary: Int, department: String)

object Worker {
  // Factory method to create a Worker instance
  def create(id: Int, name: String, city: String, salary: Int, department: String): Worker = {
    new Worker(id, name, city, salary, department)
  }
}

// Operations class to work with a list of Workers
class WorkerOps(workerList: List[Worker]) {

  // Filter workers by salary greater than or equal to the given amount
  def filterBySalary(minSalary: Int): List[Worker] = {
    workerList.filter(_.salary >= minSalary)
  }

  // Filter workers by a specific department
  def filterByDepartment(department: String): List[Worker] = {
    workerList.filter(_.department == department)
  }

  // Calculate the average salary of all workers
  def averageSalary: Int = {
    val totalSalary = workerList.map(_.salary).sum
    totalSalary / workerList.length
  }

  // Calculate the total salary of all workers
  def totalSalary: Int = {
    workerList.map(_.salary).sum
  }

  // Get the number of workers in each department
  def workersByDepartment: Map[String, Int] = {
    workerList.groupBy(_.department).map { case (department, workers) => department -> workers.length }
  }

  // Generate a formatted report with the details of all workers
  def generateReport: String = {
    val reportHeader = f"Employee Report - Total Workers: ${workerList.length}\n"
    val reportBody = workerList.map { worker =>
      f"ID: ${worker.id}%-5d | Name: ${worker.name}%-15s | City: ${worker.city}%-15s | Salary: ${worker.salary}%-8d | Department: ${worker.department}"
    }.mkString("\n")

    reportHeader + reportBody
  }
}

@main def main: Unit = {
  // Function to read worker data from a CSV file
  def readCSV(file: String): List[Worker] = {
    val lines = Source.fromFile(file).getLines().toList
    val rows = lines.tail
    var workerList: List[Worker] = List()

    rows.foreach { line =>
      val columns = line.split(",").map(_.trim)
      workerList = Worker.create(
        id = columns(0).toInt,
        name = columns(1),
        city = columns(2),
        salary = columns(3).toInt,
        department = columns(4)
      ) :: workerList
    }

    workerList
  }

  // Read workers from the CSV file
  val filename = "data.csv"
  val workersData = readCSV(filename)

  // Create a WorkerOps object to operate on the workers data
  val workerOperations = new WorkerOps(workersData)

  // Print average salary and total salary of all workers
  println(s"Average Salary: ${workerOperations.averageSalary}")
  println(s"Total Salary: ${workerOperations.totalSalary}")

  //filter workers by salary or department:
   println(workerOperations.filterBySalary(50000))
   println(workerOperations.filterByDepartment("Engineering"))

  // Print workers by department
  println(workerOperations.workersByDepartment)

  // Print the detailed report of all workers
  println(workerOperations.generateReport)
}
