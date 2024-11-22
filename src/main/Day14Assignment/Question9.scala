import org.apache.spark.sql.SparkSession

object Question9 {

  def main(args: Array[String]): Unit = {
    //9. Create an RDD of integers from 1 to 100 and write a Spark program to compute their sum using an RDD action.

    val spark = SparkSession.builder().appName("Question9").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    val data = 1 to 100
    val rdd = sc.parallelize(data)
    val sum = rdd.sum()
    println("Sum of integers from 1 to 100: "+sum)
    spark.stop()
  }

}
