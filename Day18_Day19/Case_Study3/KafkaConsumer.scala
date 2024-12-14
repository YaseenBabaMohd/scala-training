import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, current_timestamp, from_json, sum, window}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{IntegerType, StructField, StructType, TimestampType}

object KafkaConsumer {
  def main(args: Array[String]): Unit = {
    // Initialize SparkSession
    val spark = SparkSession.builder()
      .appName("KafkaConsumer")
      .master("local[*]") // Run locally with all cores
      .getOrCreate()

    // Set log level to WARN
    spark.sparkContext.setLogLevel("WARN")

    // Kafka topic and configuration
    val topic = "transactions"
    val kafkaBootstrapServers = "localhost:9092"

    // Define schema for the Kafka message payload
    val schema = StructType(Seq(
      StructField("transaction_id", IntegerType, nullable = false),
      StructField("user_id", IntegerType, nullable = false),
      StructField("amount", IntegerType, nullable = false),
      StructField("timestamp", TimestampType, nullable = false) // Ensure timestamp is in proper format
    ))

    // Read from Kafka
    val dataStreamDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

    // Parse JSON messages and select relevant fields
    val dataStream = dataStreamDF
      .select(from_json(col("value").cast("string"), schema).as("data"))
      .selectExpr("data.*")
      .withColumn("timestamp", current_timestamp()) // Add current timestamp

    // Perform windowed aggregation: sum of amounts grouped by 10-second time windows
    val aggregatedStream = dataStream
      .groupBy(window(col("timestamp"), "10 seconds"))
      .agg(sum("amount").as("total_amount"))

    // Output the results to the console
    val query = aggregatedStream.writeStream
      .outputMode("update") // Use update mode for aggregation
      .format("console") // Print to console
      .option("truncate", "false") // Avoid truncating output
      .trigger(Trigger.ProcessingTime("10 seconds")) // Trigger query every 10 seconds
      .start()

    // Await termination of the streaming query
    query.awaitTermination()
  }
}
