import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Question3 {

  //3. Create an RDD from a list of integers and filter out all even numbers using a Spark program.

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Question3").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    val data = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    val rdd = sc.parallelize(data)

    val evenNumbers = rdd.filter(x => x % 2 == 0)

    evenNumbers.collect().foreach(println)
    spark.stop()
  }

}
