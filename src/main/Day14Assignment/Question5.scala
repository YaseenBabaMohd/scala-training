import org.apache.spark.sql.SparkSession

object Question5 {

  //
  //5. Create an RDD from a list of tuples `(id, score)` and write a Spark program to calculate the average score for all records.
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Question5").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    val data = List((1, 100), (2, 200), (3, 300), (4, 400), (5, 500))
    val rdd = sc.parallelize(data)
    val averageScore = rdd.map(x => x._2).mean()
    println("Average score: "+averageScore)
    spark.stop()
  }

}
