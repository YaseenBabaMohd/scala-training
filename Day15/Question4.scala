import org.apache.spark.{SparkConf, SparkContext}

object Question4 {

  def main(args: Array[String]): Unit = {
    // Step 1: Spark Configuration and Context Creation
    val sparkConf = new SparkConf()
      .setAppName("DAGAndSparkUI")
      .setMaster("local[*]") // Run locally using all available cores
      .set("spark.executor.memory", "512m") // Limit memory for local testing

    val sc = new SparkContext(sparkConf)

    // Step 2: Create an RDD of integers from 1 to 10,000
    val numbersRDD = sc.parallelize(1 to 100, 4) // Create with 4 partitions

    // Step 3: Perform a series of transformations
    val evenNumbersRDD = numbersRDD.filter(num => num % 2 == 0) // Keep only even numbers
    val multipliedNumbersRDD = evenNumbersRDD.map(num => num * 10) // Multiply each number by 10
    val tupleNumbersRDD = multipliedNumbersRDD.flatMap(num => List((num, num + 1))) // Generate tuples (x, x+1)
    val reducedNumbersRDD = tupleNumbersRDD.reduceByKey(_ + _) // Reduce by summing keys

    // Step 4: Perform an action
    val results = reducedNumbersRDD.collect()

    // Step 5: Print the results
    println("Results:"+results.mkString(", "))

    // Keep the application running to inspect Spark UI
    println("Application running. Inspect Spark UI at http://localhost:4040 and press Ctrl+C to exit.")
    Thread.currentThread().join()

    // Stop the Spark Context (reached when the application is manually terminated)
    sc.stop()
  }

}



//Exercise 4: Exploring DAG and Spark UI
//Objective: Analyze the DAG and understand the stages involved in a complex Spark job.
//Task:
//
//Create an RDD of integers from 1 to 10,000.
//Perform a series of transformations:
//  filter: Keep only even numbers.
//map: Multiply each number by 10.
//flatMap: Generate tuples (x, x+1) for each number.
//reduceByKey: Reduce by summing keys.
//Perform an action: Collect the results.
//Expected Analysis:
//
//Analyze the DAG generated for the job and how Spark breaks it into stages.
//  Compare execution times of stages and tasks in the Spark UI.