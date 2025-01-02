import java.sql.{Connection, DriverManager, ResultSet, PreparedStatement}

case class Applicant(id: Int, fullName: String, residence: String)

object Applicant {
  def apply(id: Int, fullName: String, residence: String): Applicant = {
    new Applicant(id, fullName, residence)
  }
}

implicit def tupleToApplicant(tuple: (Int, String, String)): Applicant = {
  Applicant(tuple._1, tuple._2, tuple._3)
}

class DatabaseOperations {

  def insertApplicant(applicant: Applicant, connection: Connection): Unit = {
    try {
      val query = "INSERT INTO applicants (id, full_name, residence) VALUES (?, ?, ?)"
      val preparedStatement: PreparedStatement = connection.prepareStatement(query)
      preparedStatement.setInt(1, applicant.id)
      preparedStatement.setString(2, applicant.fullName)
      preparedStatement.setString(3, applicant.residence)
      preparedStatement.executeUpdate()
      println(s"Inserted: ${applicant.id}, ${applicant.fullName}, ${applicant.residence}")
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def insertApplicants(applicants: Array[Applicant], connection: Connection): Unit = {
    applicants.foreach(applicant => insertApplicant(applicant, connection))
  }

  def verifyInsertion(connection: Connection): Unit = {
    try {
      val query = "SELECT * FROM applicants"
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(query)
      println("Applicants in the database:")
      while (resultSet.next()) {
        val id = resultSet.getInt("id")
        val fullName = resultSet.getString("full_name")
        val residence = resultSet.getString("residence")
        println(s"ID: $id, Full Name: $fullName, Residence: $residence")
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}

@main def main: Unit = {
  val dbUrl = "jdbc:mysql://localhost:3306/mydb"
  val username = "root"
  val password = "root@123"

  var connection: Connection = null

  try {
    Class.forName("com.mysql.cj.jdbc.Driver")
    connection = DriverManager.getConnection(dbUrl, username, password)

    val applicantData: Array[(Int, String, String)] = Array(
      (1, "Alice", "New York"),
      (2, "Bob", "Los Angeles"),
      (3, "Charlie", "Chicago"),
      (4, "Diana", "Houston"),
      (5, "Eve", "Phoenix"),
      (6, "Frank", "Philadelphia"),
      (7, "Grace", "San Antonio"),
      (8, "Hank", "San Diego"),
      (9, "Ivy", "Dallas"),
      (10, "Jack", "San Jose"),
      (11, "Kathy", "Austin"),
      (12, "Leo", "Jacksonville"),
      (13, "Mona", "Fort Worth"),
      (14, "Nina", "Columbus"),
      (15, "Oscar", "Charlotte"),
      (16, "Paul", "San Francisco"),
      (17, "Quinn", "Indianapolis"),
      (18, "Rita", "Seattle"),
      (19, "Steve", "Denver"),
      (20, "Tina", "Washington"),
      (21, "Uma", "Boston"),
      (22, "Vince", "El Paso"),
      (23, "Wendy", "Detroit"),
      (24, "Xander", "Nashville"),
      (25, "Yara", "Portland"),
      (26, "Zane", "Oklahoma City"),
      (27, "Aiden", "Las Vegas"),
      (28, "Bella", "Louisville"),
      (29, "Caleb", "Baltimore"),
      (30, "Daisy", "Milwaukee"),
      (31, "Ethan", "Albuquerque"),
      (32, "Fiona", "Tucson"),
      (33, "George", "Fresno"),
      (34, "Hazel", "Mesa"),
      (35, "Ian", "Sacramento"),
      (36, "Jill", "Atlanta"),
      (37, "Kyle", "Kansas City"),
      (38, "Luna", "Colorado Springs"),
      (39, "Mason", "Miami"),
      (40, "Nora", "Raleigh"),
      (41, "Owen", "Omaha"),
      (42, "Piper", "Long Beach"),
      (43, "Quincy", "Virginia Beach"),
      (44, "Ruby", "Oakland"),
      (45, "Sam", "Minneapolis"),
      (46, "Tara", "Tulsa"),
      (47, "Ursula", "Arlington"),
      (48, "Victor", "New Orleans"),
      (49, "Wade", "Wichita"),
      (50, "Xena", "Cleveland")
    )

    val applicants = applicantData.map(tuple => tuple: Applicant)
    val dbOps = new DatabaseOperations()
    dbOps.insertApplicants(applicants, connection)
    dbOps.verifyInsertion(connection)

  } catch {
    case e: Exception => e.printStackTrace()
  } finally {
    if (connection != null) connection.close()
  }
}
