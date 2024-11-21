import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Question2 {

  //2. Create two RDDs containing numbers and write a Spark program to compute their Cartesian product using RDD transformations.
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Question2").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    val data1 = List(1, 2, 3, 4, 5)
    val data2 = List(6, 7, 8, 9, 10)

    val rdd1: RDD[Int] = sc.parallelize(data1)
    val rdd2: RDD[Int] = sc.parallelize(data2)

    val cartesianProduct = rdd1.flatMap(x => rdd2.map(y => (x, y)))
    cartesianProduct.collect().foreach(println)
    spark.stop()
  }
}
