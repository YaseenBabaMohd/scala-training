import casestudy.operations.{AggregationMetrics, StorageOptimisation}
import org.apache.spark.sql.{SaveMode, SparkSession, functions}
import org.apache.spark.sql.functions.{col, when}


//1. Data Preparation
//Load and preprocess the Walmart Recruiting Dataset. The dataset includes:
//• - train.csv: Contains historical weekly sales data for various stores and departments.
//• - features.csv: Contains additional data about stores and regions.
//• - stores.csv: Contains store metadata.
object CaseStudy4 {
  def main(args: Array[String]): Unit = {

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

    featuresDF.show()
    trainDF.show()
    storesDF.show()



    //2. Data Validation and Enrichment
    //Validate the sales data and enrich it with metadata by performing the following steps:
    //• - Ensure no missing or invalid values in critical columns.
    //• - Filter out records where Weekly_Sales is negative.
    val validatedTrainDF = trainDF.na.drop("any", Seq("Store", "Dept", "Weekly_Sales", "Date"))
    val validatedStoresDF = storesDF.na.drop("any", Seq("Store", "Type", "Size"))
    val validatedFeaturesDF = featuresDF.na.drop("any", Seq("Store", "Date"))

    validatedTrainDF.select(
      functions.count(when(col("Store").isNull, 1)).as("Store_missing"),
      functions.count(when(col("Dept").isNull, 1)).as("Dept_missing"),
      functions.count(when(col("Date").isNull, 1)).as("Date_missing"),
      functions.count(when(col("Weekly_Sales").isNull, 1)).as("Weekly_Sales_missing")
    ).show()

    val filteredTrainDF = validatedTrainDF.filter(col("Weekly_Sales") >= 0)
    trainDF.filter(col("Weekly_Sales") < 0).show()




    //Caching Scenario: Cache the features.csv and stores.csv datasets after cleaning, as they will be used repeatedly in multiple join operations.
    validatedFeaturesDF.cache()
    validatedStoresDF.cache()

    //BroadCasting the store as the store data is small
    val broadcastedStoresDF = functions.broadcast(validatedStoresDF)

    //Perform joins with features.csv and stores.csv on relevant keys.
    val enrichedDF = filteredTrainDF
      .join(featuresDF.withColumnRenamed("IsHoliday", "Feature_IsHoliday"), Seq("Store", "Date"), "left")
      .join(storesDF, Seq("Store"), "left")
    enrichedDF.cache()


   // StorageOptimisation.saveAsParquet(enrichedDF, "enrichedDF")


    enrichedDF.show()
    //3. Aggregation and Analysis
    //Aggregate the enriched data to derive insights and answer the following questions:
    //• - Store-Level Metrics: Total weekly sales, average weekly sales, and top-performing stores.
    AggregationMetrics.storeLevelMetrics(enrichedDF)

    // Department-Level Metrics: Total sales, weekly trends, and holiday vs. non-holiday sales.
    AggregationMetrics.DepartmentLevelMetrics(enrichedDF)

    // Storage Optimization
    //- Partitioning the data by Store and Date
    val filePath = "gs://casestudy_datasets/result/partitionData"
    val finalDF = enrichedDF.limit(1000)
    finalDF.write
      .mode(SaveMode.Overwrite)
      .partitionBy("Store", "Date")
      .parquet(filePath)



   spark.stop()


  }
}

