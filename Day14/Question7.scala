import org.apache.spark.sql.SparkSession

object Question7 {


  //7. Write a Spark program to perform a union operation on two RDDs of integers and remove duplicate elements from the resulting RDD.

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Question7").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    val data1 = List(1, 2, 3, 4, 5)
    val data2 = List(4, 5, 6, 7, 8)

    val rdd1 = sc.parallelize(data1)
    val rdd2 = sc.parallelize(data2)

    val unionRDD = rdd1.union(rdd2).distinct()
    unionRDD.collect().foreach(println)
    spark.stop()
  }

}
