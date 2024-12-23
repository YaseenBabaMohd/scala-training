import org.apache.spark.sql.{SaveMode, SparkSession, functions}
import org.apache.spark.sql.functions.{col, when, lag, sum, avg, count, desc, broadcast}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.storage.StorageLevel

object CaseStudy4 {
  def main(args: Array[String]): Unit = {

    val gsServiceAccJsonPath = "/Users/yaseenbabamohammad/Documents/gcpkey.json"
    val spark = SparkSession.builder()
      .appName("Walmart Data Analysis")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", gsServiceAccJsonPath)
      .config("spark.hadoop.fs.gs.auth.service.account.debug", "true")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext
    sc.setLogLevel("ERROR")

    val featuresPath = "gs://casestudy_datasets/Datasets/features.csv"
    val trainPath = "gs://casestudy_datasets/Datasets/train.csv"
    val storesPath = "gs://casestudy_datasets/Datasets/stores.csv"

    val featuresDF = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(featuresPath)

    val trainDF = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(trainPath)

    val storesDF = spark.read.option("header", "true")
      .option("inferSchema", "true")
      .csv(storesPath)

    // 1. Data Validation and Enrichment
    // Ensure no missing or invalid values in critical columns
    val validatedTrainDF = trainDF.na.drop("any", Seq("Store", "Dept", "Weekly_Sales", "Date"))
    val validatedStoresDF = storesDF.na.drop("any", Seq("Store", "Type", "Size"))
    val validatedFeaturesDF = featuresDF.na.drop("any", Seq("Store", "Date"))

    // Filter out records where Weekly_Sales is negative
    val filteredTrainDF = validatedTrainDF.filter(col("Weekly_Sales") >= 0)

    // Cache the features and stores DataFrames for repeated use
    validatedFeaturesDF.cache()
    validatedStoresDF.cache()

    // Broadcast the stores DataFrame (small dataset)
    val broadcastedStoresDF = broadcast(validatedStoresDF)

    // Perform joins with features.csv and stores.csv on relevant keys
    val enrichedDF = filteredTrainDF
      .join(validatedFeaturesDF.withColumnRenamed("IsHoliday", "Feature_IsHoliday"), Seq("Store", "Date"), "left")
      .join(broadcastedStoresDF, Seq("Store"), "left")

    enrichedDF.cache()

    // 2. Aggregation and Analysis
    // Store-Level Metrics
    val storeMetrics = enrichedDF.groupBy("Store")
      .agg(
        sum("Weekly_Sales").alias("Total_Weekly_Sales"),
        avg("Weekly_Sales").alias("Average_Weekly_Sales"),
        count("Store").alias("Data_Count")
      )
    storeMetrics.show()

    // Department-Level Metrics
    val departmentMetrics = enrichedDF.groupBy("Store", "Dept")
      .agg(
        sum("Weekly_Sales").alias("Total_Weekly_Sales"),
        avg("Weekly_Sales").alias("Average_Weekly_Sales"),
        count("Store").alias("Data_Count")
      )
    departmentMetrics.show()

    // Weekly Trends: Calculate sales difference from previous week
    val windowSpec = Window.partitionBy("Store", "Dept").orderBy("Date")
    val weeklyTrendsDF = enrichedDF
      .withColumn("Previous_Weekly_Sales", lag("Weekly_Sales", 1).over(windowSpec))
      .withColumn("Weekly_Trend", col("Weekly_Sales") - col("Previous_Weekly_Sales"))
      .select("Store", "Dept", "Date", "Weekly_Sales", "IsHoliday", "Previous_Weekly_Sales", "Weekly_Trend")

    weeklyTrendsDF.show()

    // Holiday vs Non-Holiday Sales
    val holidaySales = enrichedDF.filter("IsHoliday = true")
      .groupBy("Store", "Dept")
      .agg(sum("Weekly_Sales").alias("Holiday_Sales"))

    val nonHolidaySales = enrichedDF.filter("IsHoliday = false")
      .groupBy("Store", "Dept")
      .agg(sum("Weekly_Sales").alias("NonHoliday_Sales"))

    val holidayComparison = holidaySales
      .join(nonHolidaySales, Seq("Store", "Dept"), "outer")
      .orderBy(desc("Holiday_Sales"))

    holidayComparison.show()

    // 3. Storage Optimization
    // Partition the data by Store and Date and save it to Parquet
    val partitionedDataPath = "gs://casestudy_datasets/result/partitioned_data"
    enrichedDF.write
      .mode(SaveMode.Overwrite)
      .partitionBy("Store", "Date")
      .parquet(partitionedDataPath)

    // Optionally save the metrics as JSON for further analysis
    val storeMetricsPath = "gs://casestudy_datasets/aggregated_metrics/store_level"
    storeMetrics.write
      .mode(SaveMode.Overwrite)
      .json(storeMetricsPath)

    val departmentMetricsPath = "gs://casestudy_datasets/aggregated_metrics/department_level"
    departmentMetrics.write
      .mode(SaveMode.Overwrite)
      .json(departmentMetricsPath)

    val holidayVsNonHolidayMetricsPath = "gs://casestudy_datasets/aggregated_metrics/holiday_vs_non_holiday"
    holidayComparison.write
      .mode(SaveMode.Overwrite)
      .json(holidayVsNonHolidayMetricsPath)



    spark.stop()
  }
}
