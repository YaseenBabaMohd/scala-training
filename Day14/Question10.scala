import org.apache.spark.sql.SparkSession

object Question10 {

  def main(args: Array[String]): Unit = {

    ///10. Write a Spark program to group an RDD of key-value pairs `(key, value)` by key and compute the sum of values for each key.
    val spark = SparkSession.builder().appName("Question10").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    val data = List(("A", 1), ("B", 2), ("C", 3), ("A", 4), ("B", 5), ("C", 6))
    val rdd = sc.parallelize(data)
    val result = rdd.reduceByKey(_ + _)
    result.collect().foreach(println)
    spark.stop()
  }

}
