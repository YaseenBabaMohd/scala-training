import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.rdd.RDD

object Question8 {
  def main(args: Array[String]): Unit = {

    // Initialize Spark session
    val spark = SparkSession.builder
      .appName("CSV Reader")
      .master("local[*]") // Use local mode, remove or change in production
      .getOrCreate()

    // Path to the CSV file
    val filePath = "src/main/resources/converted_data.csv"

    // Read the CSV file with header option enabled and schema inference
    val df = spark.read
      .option("header", "true")   // Ensures the first row is treated as a header
      .option("inferSchema", "true") // Automatically infers the schema for numeric fields
      .csv(filePath)

    // Convert the DataFrame to an RDD for further processing if needed
    val rdd: RDD[Row] = df.rdd

    // Process each row in the RDD
    rdd.foreach { row =>
        // Extract columns with their appropriate types
        val id = row.getAs[Int]("id")                // Extract id as an integer
        val name = row.getAs[String]("name")         // Extract name as a string
        val age = row.getAs[Int]("age")       // Extract other as a string

        // Further processing with valid data
        println(s" ID: $id, Name: $name, Age: $age")
    }

    // Stop the Spark session
    spark.stop()
  }
}
