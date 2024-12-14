import org.apache.spark.{SparkConf, SparkContext}

object Question3 {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setAppName("AnalyzingTasksAndExecutors")
      .setMaster("local[*]") // Run locally using all available cores
      .set("spark.executor.memory", "512m") // Limit memory for local testing
      .set("spark.executor.instances", "2") // Set executor instances to 2

    val sc = new SparkContext(sparkConf)

    // Step 2: Create an RDD of strings with at least 1 million lines
    val numLines = 1000000
    val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " * numLines
    val textRDD = sc.parallelize(text.split("\\s+"), 4)

    // Step 3: Perform a transformation pipeline
    val wordCountRDD = textRDD
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    wordCountRDD.collect().foreach(println)

    // Keep the application running to inspect Spark UI
    println("Application running. Inspect Spark UI at http://localhost:4040 and press Ctrl+C to exit.")
    Thread.currentThread().join()
    sc.stop()


  }

}


//Exercise 3: Analyzing Tasks and Executors
//  Objective: Understand how tasks are distributed across executors in local mode.
//Task:
//
//  Create an RDD of strings with at least 1 million lines (e.g., lorem ipsum or repetitive text).
//  Perform a transformation pipeline:
//  Split each string into words.
//  Map each word to (word, 1).
//Reduce by key to count word occurrences.
//  Set spark.executor.instances to 2 and observe task distribution in the Spark UI.
//  Expected Analysis:
//
//  Compare task execution times across partitions and stages in the UI.
//  Understand executor and task allocation for a local mode Spark job.
