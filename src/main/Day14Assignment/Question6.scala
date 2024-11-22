import org.apache.spark.sql.SparkSession

object Question6 {

  //6. Create two RDDs containing key-value
  // pairs `(id, name)` and `(id, score)`. Write a Spark program to join these RDDs on `id` and produce `(id, name, score)`.

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Question6").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    val data1 = List((1, "Alice"), (2, "Bob"), (3, "Charlie"), (4, "David"), (5, "Eva"))
    val data2 = List((1, 100), (2, 200), (3, 300), (4, 400), (5, 500))

    val rdd1 = sc.parallelize(data1)
    val rdd2 = sc.parallelize(data2)

    val joinedRDD = rdd1.join(rdd2)
    val formattedRDD = joinedRDD.map {
      case (key, (value2, value3)) => (key, value2, value3)
    }
    formattedRDD.collect().foreach(println)
    spark.stop()
  }
}
