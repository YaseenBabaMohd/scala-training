package casestudy.kafka

import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.Encoders
import org.apache.spark.storage.StorageLevel

// Define case class for SalesRecord outside main method to make it globally accessible
case class SalesRecord(
                        store: String,         // Store identifier
                        dept: String,          // Department identifier
                        date: String,          // Date of the sales record
                        weeklySales: Float,    // Weekly sales amount
                        isHoliday: Boolean     // Whether it's a holiday
                      )

object SalesConsumer {

  // Initialize SparkSession
  val gsServiceAccJsonPath = "/Users/yaseenbabamohammad/Documents/gcpkey.json"
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

  // GCS Configuration
  val featuresPath = "gs://casestudy_datasets/Datasets/features.csv"
  val storesPath = "gs://casestudy_datasets/Datasets/stores.csv"
  val trainPath = s"gs://casestudy_datasets/Datasets/updated_train.csv"
  val storeWiseMetricsPath = "gs://casestudy_datasets/result/avgWeeklySalesByStore"
  val deptWiseMetricsPath = "gs://casestudy_datasets/result/totalSalesByDept"
  val holidayVsNonHolidayMetricsPath = "gs://casestudy_datasets/result/holidayAndNonHolidayPath"
  var existingStoreMetrics: DataFrame = null
  var existingDepartmentMetrics: DataFrame = null
  var existingHolidayMetrics: DataFrame = null

  def main(args: Array[String]): Unit = {

    // Load from GCS
    val rawFeaturesDF = spark.read
      .option("header", "true") // Adjusted for datasets saved with headers
      .option("inferSchema", "true")
      .csv(featuresPath)

    val rawStoresDF = spark.read
      .option("header", "true") // Adjusted for datasets saved with headers
      .option("inferSchema", "true")
      .csv(storesPath)

    existingStoreMetrics = spark.read
      .json(storeWiseMetricsPath)

    existingDepartmentMetrics = spark.read
      .json(deptWiseMetricsPath)

    existingHolidayMetrics = spark.read
      .json(holidayVsNonHolidayMetricsPath)

    // Validate critical columns for featuresDF and storesDF
    // Cache the features
    val featuresDF = rawFeaturesDF.na.drop("any", Seq("Store", "Date")).cache()
    // Broadcast the stores since it is very small (45 rows)
    val storesDF = broadcast(rawStoresDF.na.drop("any", Seq("Store", "Type", "Size")))
    spark.sparkContext.setLogLevel("WARN")

    // Kafka configuration
    val kafkaBootstrapServers = "localhost:9092"
    val topic = "sales-report"

    // Read messages from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

    // Define encoder for SalesRecord (case class)
    implicit val salesRecordEncoder = Encoders.product[SalesRecord]

    // Parse JSON messages to create DataFrame
    val salesDF = kafkaDF
      .selectExpr("CAST(value AS STRING) as json") // Extract JSON string
      .select(from_json($"json", salesRecordEncoder.schema).as("salesRecord")) // Parse JSON string to structured data
      .select("salesRecord.*") // Flatten the struct
      .na.fill(Map(
        "isHoliday" -> false,        // Default boolean value
        "weeklySales" -> 0.0f        // Default float value
      ))
      .select(
        $"store".alias("Store"),
        $"dept".alias("Dept"),
        $"date".alias("Date"),
        $"weeklySales".alias("Weekly_Sales"),
        $"isHoliday".alias("IsHoliday")
      )

    // Validate and filter records
    val validatedSalesDF = salesDF.filter($"Weekly_Sales" >= 0).na.drop("any", Seq("Store", "Dept", "Weekly_Sales", "Date"))

    // Process new records
    val query = validatedSalesDF.writeStream
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>
        println(s"Processing batch: $batchId")
        batchDF.show()
        val newTrainBatch = batchDF.cache()

        // Append new records to updated_train.csv in GCS
        newTrainBatch.write.mode(SaveMode.Append).option("header", "true").csv(trainPath)
        // Update partitioned enriched data and metrics in GCS
        updateEnrichedAndMetrics(newTrainBatch, featuresDF, storesDF)

        println(s"Batch $batchId processed successfully")
      }
      .start()

    query.awaitTermination() // Ensure the query runs until terminated
    spark.stop()
  }

  // Function to update enriched data and metrics
  def updateEnrichedAndMetrics(newTrainBatch: DataFrame,
                               featuresDF: DataFrame,
                               storesDF: DataFrame): Unit = {

    val enrichedPath = "gs://casestudy_datasets/result/totalSalesByDept"

    // Join new train batch with features and store details to create new enriched data
    val newEnrichedData = newTrainBatch
      .join(featuresDF, Seq("Store", "Date", "IsHoliday"), "left")
      .join(storesDF, Seq("Store"), "left")

    // Append new enriched data to the enriched path
    newEnrichedData.write
      .mode(SaveMode.Append)
      .partitionBy("Store", "Date")
      .parquet(enrichedPath)

    // Calculate updated aggregated metrics based on newTrainBatch and existingAggregatedMetrics

    //Update store wise metrics
    val updatedStoreMetrics = computeUpdatedStoreMetrics(newTrainBatch, existingStoreMetrics).persist(StorageLevel.MEMORY_ONLY)
    updatedStoreMetrics.write.mode(SaveMode.Overwrite).json(storeWiseMetricsPath)
    existingStoreMetrics = updatedStoreMetrics
    existingStoreMetrics.show(10)

    // Top-Performing Stores (assuming "performance" is based on total weekly sales)
//    val topStores = existingStoreMetrics.orderBy(desc("Total_Weekly_Sales")).limit(5)
//    println("Top-Performing Stores:")
//    topStores.show()
//
//    //Update department wise metrics
//    val updatedDepartmentMetrics = computeUpdatedDepartmentMetrics(newTrainBatch, existingDepartmentMetrics).persist(StorageLevel.MEMORY_AND_DISK_SER)
//    updatedDepartmentMetrics.write.mode(SaveMode.Overwrite).json(deptWiseMetricsPath)
//    existingDepartmentMetrics = updatedDepartmentMetrics
//    //existingDepartmentMetrics.show(10)
//
//    //Update holiday vs non holiday sales metrics
//    val updatedHolidayMetrics = computeUpdatedHolidayVsNonHolidayMetrics(newTrainBatch, existingHolidayMetrics).persist(StorageLevel.MEMORY_AND_DISK)
//    updatedHolidayMetrics.write.mode(SaveMode.Overwrite).json(storeWiseMetricsPath)
//    existingHolidayMetrics = updatedHolidayMetrics
//    //existingHolidayMetrics.show(10)

    println(s"Metrics are updated.")
  }

  def computeUpdatedStoreMetrics(newTrainBatch: DataFrame,
                                 existingStoreMetrics: DataFrame): DataFrame = {
    val newStoreMetrics = newTrainBatch.groupBy("Store")
      .agg(
        sum("Weekly_Sales").alias("New_Total_Weekly_Sales"),
        avg("Weekly_Sales").alias("New_Average_Weekly_Sales"),
        count("Weekly_Sales").alias("New_Data_Count")
      )

    val updatedStoreMetrics = if (existingStoreMetrics != null) {
      // Perform the outer join and compute updated metrics
      newStoreMetrics.join(existingStoreMetrics, Seq("Store"), "outer")
        .select(
          coalesce($"Store", $"Store").alias("Store"),
          (coalesce($"New_Total_Weekly_Sales", lit(0.0)) + coalesce($"Total_Weekly_Sales", lit(0.0))).alias("Total_Weekly_Sales"),
          // Compute weighted average for proper average calculation
          (
            ((coalesce($"New_Average_Weekly_Sales", lit(0.0)) * coalesce($"New_Data_Count", lit(0))) +
              (coalesce($"Average_Weekly_Sales", lit(0.0)) * coalesce($"Data_Count", lit(0))))
              /
              (coalesce($"New_Data_Count", lit(0)) + coalesce($"Data_Count", lit(0)))
            ).alias("Average_Weekly_Sales"),
          (coalesce($"New_Data_Count", lit(0)) + coalesce($"Data_Count", lit(0))).alias("Data_Count") // Maintain counts
        )
    } else {
      // If no existing metrics, save new metrics directly
      newStoreMetrics.select(
        $"Store",
        $"New_Total_Weekly_Sales".alias("Total_Weekly_Sales"),
        $"New_Average_Weekly_Sales".alias("Average_Weekly_Sales"),
        $"New_Data_Count".alias("Data_Count")
      )
    }

    updatedStoreMetrics
  }

  def computeUpdatedDepartmentMetrics(newTrainBatch: DataFrame,
                                      existingDepartmentMetrics: DataFrame): DataFrame = {
    val newDeptMetrics = newTrainBatch.groupBy("Store", "Dept")
      .agg(
        sum("Weekly_Sales").alias("New_Total_Weekly_Sales"),
        avg("Weekly_Sales").alias("New_Average_Weekly_Sales"),
        count("Store").alias("New_Data_Count")
      )

    val updatedDeptMetrics = if (existingDepartmentMetrics != null) {
      // Perform the outer join and compute updated metrics
      newDeptMetrics.join(existingDepartmentMetrics, Seq("Store", "Dept"), "outer")
        .select(
          coalesce($"Store", $"Store").alias("Store"),
          coalesce($"Dept", $"Dept").alias("Dept"),
          (coalesce($"New_Total_Weekly_Sales", lit(0.0)) + coalesce($"Total_Weekly_Sales", lit(0.0))).alias("Total_Weekly_Sales"),
          // Compute weighted average for proper average calculation
          (
            ((coalesce($"New_Average_Weekly_Sales", lit(0.0)) * coalesce($"New_Data_Count", lit(0))) +
              (coalesce($"Average_Weekly_Sales", lit(0.0)) * coalesce($"Data_Count", lit(0))))
              /
              (coalesce($"New_Data_Count", lit(0)) + coalesce($"Data_Count", lit(0)))
            ).alias("Average_Weekly_Sales"),
          (coalesce($"New_Data_Count", lit(0)) + coalesce($"Data_Count", lit(0))).alias("Data_Count")
        )
    } else {
      // If no existing metrics, save new metrics directly
      newDeptMetrics.select(
        $"Store",
        $"Dept",
        $"New_Total_Weekly_Sales".alias("Total_Weekly_Sales"),
        $"New_Average_Weekly_Sales".alias("Average_Weekly_Sales"),
        $"New_Data_Count".alias("Data_Count")
      )
    }

    updatedDeptMetrics
  }

  def computeUpdatedHolidayVsNonHolidayMetrics(newTrainBatch: DataFrame,
                                               existingHolidayMetrics: DataFrame): DataFrame = {
    val newHolidaySales = newTrainBatch.filter($"IsHoliday" === true)
      .groupBy("Store", "Dept")
      .agg(sum("Weekly_Sales").alias("New_Holiday_Sales"))

    val newNonHolidaySales = newTrainBatch.filter($"IsHoliday" === false)
      .groupBy("Store", "Dept")
      .agg(sum("Weekly_Sales").alias("New_NonHoliday_Sales"))

    val newHolidayMetrics = newHolidaySales.join(newNonHolidaySales, Seq("Store", "Dept"), "outer")
      .select(
        coalesce($"Store", $"Store").alias("Store"),
        coalesce($"Dept", $"Dept").alias("Dept"),
        coalesce($"New_Holiday_Sales", lit(0.0)).alias("New_Holiday_Sales"),
        coalesce($"New_NonHoliday_Sales", lit(0.0)).alias("New_NonHoliday_Sales")
      )

    val updatedHolidayMetrics = if (existingHolidayMetrics != null) {
      // Perform the outer join and compute updated metrics
      newHolidayMetrics.join(existingHolidayMetrics, Seq("Store", "Dept"), "outer")
        .select(
          coalesce($"Store", $"Store").alias("Store"),
          coalesce($"Dept", $"Dept").alias("Dept"),
          (coalesce($"New_Holiday_Sales", lit(0.0)) + coalesce($"Holiday_Sales", lit(0.0))).alias("Holiday_Sales"),
          (coalesce($"New_NonHoliday_Sales", lit(0.0)) + coalesce($"NonHoliday_Sales", lit(0.0))).alias("NonHoliday_Sales")
        )
    } else {
      // If no existing metrics, save new metrics directly
      newHolidayMetrics.select(
        $"Store",
        $"Dept",
        $"New_Holiday_Sales".alias("Holiday_Sales"),
        $"New_NonHoliday_Sales".alias("NonHoliday_Sales")
      )
    }

    updatedHolidayMetrics
  }
}
