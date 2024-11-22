import org.apache.spark.sql.SparkSession

object Question4 {

  //4. Write a Spark program to count the frequency of each character in a given collection of strings using RDD transformations.

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Question4").master("local[*]").getOrCreate()

    val sc = spark.sparkContext
    val data = List("Hello World", "Hello Scala", "Hello Spark", "Hello Hadoop")

    val rdd = sc.parallelize(data)

    val characters = rdd.flatMap(x => x.split(""))
    val characterCount = characters.map(x => (x, 1)).reduceByKey(_ + _)
    println("Character frequency:"+characterCount.collect().mkString(", "))
    spark.stop()


  }

}
