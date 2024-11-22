import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.rdd.RDD

object CSVReaderApp {
  def main(args: Array[String]): Unit = {

    // Initialize Spark session
    val spark = SparkSession.builder
      .appName("CSV Reader")
      .master("local[*]") // Use local mode, remove or change in production
      .getOrCreate()

    // Path to the CSV file
    val filePath = "/Users/yaseenbabamohammad/Desktop/Scala_DE_Training/Day14Assignment/src/main/resources/converted_data.csv"

    // Read the CSV file with header option enabled to skip the first row (header)
    val df = spark.read
      .option("header", "true")  // Ensures the first row is treated as a header
      .option("inferSchema", "true") // Automatically infers the schema for numeric fields
      .csv(filePath)

    // Convert the DataFrame to an RDD for further processing if needed
    val rdd: RDD[Row] = df.rdd

    // Process each row in the RDD
    rdd.foreach { row =>
      try {
        // Example: Extract "id" column and convert it to an integer
        val idString = row.getAs[String]("id")

        // Try parsing "id" as an integer (handle invalid rows gracefully)
        val id = if (idString != null && idString.forall(_.isDigit)) {
          idString.toInt  // Safely parse to integer if valid
        } else {
          throw new NumberFormatException(s"Invalid ID: $idString") // Handle invalid values
        }

        // Further processing with valid "id"
        println(s"Processing row with valid ID: $id")

      } catch {
        case e: NumberFormatException =>
          // Handle invalid rows (skip or log them)
          println(s"Skipping invalid row due to error: ${e.getMessage}")
        case e: Exception =>
          // Catch any other exceptions that may occur
          println(s"Unexpected error while processing row: ${row}, Error: ${e.getMessage}")
      }
    }

    // Stop the Spark session
    spark.stop()
  }
}
