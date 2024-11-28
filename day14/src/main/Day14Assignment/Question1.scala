import



//1. Given a collection of strings,
// write a Spark program to count the total number of words in the collection using RDD transformations and actions.
object Question1 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext
    val data = List("Hello World", "Hello Scala", "Hello Spark", "Hello Hadoop")
    val rdd = sc.parallelize(data)

    val words = rdd.flatMap(x => x.split(" "))
    val wordCount = words.count()

    println("Total number of words: "+wordCount)

    spark.stop()
  }
}
