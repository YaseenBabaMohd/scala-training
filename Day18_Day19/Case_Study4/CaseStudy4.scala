import org.apache.spark.sql.{SparkSession, functions}

object CaseStudy4 {
  def main(args: Array[String]): Unit = {
    val gsServiceAccJsonPath = "/Users/yaseenbabamohammad/spark-gcs-key.json"

    val gsInputPath = "gs://artifacts_spark_jobs/movie_metadata/yaseen/input"
    val gsOutputPath = "gs://artifacts_spark_jobs/movie_metadata/yaseen/output"


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

    val sc = spark.sparkContext

    def generateData: Int => Seq[(Int, String)] = n => {
      // Create a range of numbers from 1 to n
      (1 to n).map { num =>
        // Check if the number is even or odd
        if (num % 2 == 0)
          (num, "IN_PROGRESS")  // Pair even numbers with "IN_PROGRESS"
        else
          (num, "COMPLETED")    // Pair odd numbers with "COMPLETED"
      }
    }


    val dataRDD = sc.parallelize(generateData(100000))
    dataRDD.toDF("id", "status").coalesce(1).write.mode("overwrite").parquet(gsInputPath)

    val inputData = spark.read.parquet(gsInputPath)

    val filterData = inputData.filter($"status" === "COMPLETED")
    filterData.show()


    filterData.toDF("id", "status").coalesce(1).write.mode("overwrite").parquet(gsOutputPath)

    spark.stop()




  }
}
