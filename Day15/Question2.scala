import org.apache.spark.{SparkConf, SparkContext}

object Question2 {
  def main(args: Array[String]): Unit = {
    // Step 1: Spark Configuration and Context Creation
    val sparkConf = new SparkConf()
      .setAppName("NarrowVsWideTransformations")
      .setMaster("local[*]") // Run locally using all available cores
      .set("spark.executor.memory", "512m") // Limit memory for local testing

    val sc = new SparkContext(sparkConf)

    // Step 2: Create an RDD of numbers from 1 to 1000
    val numbersRDD = sc.parallelize(1 to 1000, 4) // Create with 4 partitions

    // Step 3: Apply Narrow Transformations
    val squaredNumbersRDD = numbersRDD.map(num => num * num) // Narrow transformation (map)
    val evenNumbersRDD = squaredNumbersRDD.filter(num => num % 2 == 0) // Narrow transformation (filter)

    // Step 4: Apply Wide Transformation
    // Map numbers to key-value pairs based on number % 10, then use reduceByKey (wide transformation)
    val keyValueRDD = evenNumbersRDD.map(num => (num % 10, num))
    val reducedByKeyRDD = keyValueRDD.reduceByKey(_ + _) // Wide transformation, causes a shuffle

    // Step 5: Save the Result to a Text File
    val outputPath = "src/main/result/narrow_vs_wide_transformations"
    reducedByKeyRDD.saveAsTextFile(outputPath)

    // Keep the application running to inspect Spark UI
    println("Application running. Inspect Spark UI at http://localhost:4040 and press Ctrl+C to exit.")
    Thread.currentThread().join()

    // Stop the Spark Context (reached when the application is manually terminated)
    sc.stop()
  }
}


//Exercise 2: Narrow vs Wide Transformations
//  Objective: Differentiate between narrow and wide transformations in Spark.
//Task:
//
//  Create an RDD of numbers from 1 to 1000.
//Apply narrow transformations: map, filter.
//  Apply a wide transformation: groupByKey or reduceByKey (simulate by mapping numbers into key-value pairs, e.g., (number % 10, number)).
//  Save the results to a text file.
//  Expected Analysis:
//
//  Identify how narrow transformations execute within a single partition, while wide transformations cause shuffles.
//Observe the DAG in the Spark UI, focusing on stages and shuffle operations.

