import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{broadcast, coalesce, col, from_json, lit}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StructField, StructType}

object KafkaMessageConsumer {

  def main(args: Array[String]): Unit = {
    val gsServiceAccJsonPath = "/Users/yaseenbabamohammad/spark-gcs-key.json"

    val gsPath = "gs://artifacts_spark_jobs/movie_metadata/yaseen/user_details.csv"
    val gsEnrichedDataOutputPath = "gs://artifacts_spark_jobs/movie_metadata/yaseen/enriched_data"
    val gsEnrichedDataCheckpointPath = "gs://artifacts_spark_jobs/movie_metadata/yaseen/enriched_data_checkpoint"
    val topic = "orders"
    val spark = SparkSession.builder()
      .appName("JSON to CSV Conversion")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", gsServiceAccJsonPath)
      .config("spark.hadoop.fs.gs.auth.service.account.debug", "true")
      .master("local[*]")
      .getOrCreate()
     import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")
    val userData = spark.read.option("header", "true").csv(gsPath)

    userData.show()

    val kafkaStreamDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

    val ordersSchema = StructType(Seq(
      StructField("order_id", IntegerType),
      StructField("user_id", IntegerType),
      StructField("amount", DoubleType)
    ))

    val parsedDF = kafkaStreamDF.selectExpr("CAST(value AS STRING) as json")
      .select(from_json(col("json"), ordersSchema).as("data"))
      .select("data.order_id", "data.user_id", "data.amount")

    val enrichedDF = parsedDF
      .join(broadcast(userData), Seq("user_id"), "left_outer") // some orders may not contain user_id details
      .select(
        col("order_id"),
        col("user_id"),
        col("amount"),
        coalesce(col("name"), lit("UNKNOWN")).alias("name"), // Default name
        coalesce(col("age"), lit(0)).alias("age"), // Default age
        coalesce(col("email"), lit("NOT_AVAILABLE")).alias("email") // Default email
      )

    // Write the stream to GCS
    val query = enrichedDF.writeStream
      .outputMode("append") // Append mode for continuous data
      .format("json") // Write as JSON
      .option("path", gsEnrichedDataOutputPath)
      .option("checkpointLocation", gsEnrichedDataCheckpointPath)
      .start()

    query.awaitTermination()



  }

}
