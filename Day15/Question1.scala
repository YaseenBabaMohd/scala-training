import org.apache.spark.{SparkConf, SparkContext}
import scala.util.Random

object Question1 {
  def main(args: Array[String]): Unit = {
    // Step 1: Spark Configuration and Context Creation
    val sparkConf = new SparkConf()
      .setAppName("RDDPartitioningExercise")
      .setMaster("local[*]")         // Use all available cores for local execution
      .set("spark.executor.memory", "512m") // Set executor memory to 512 MB

    val sc = new SparkContext(sparkConf)

    // Step 2: Generate an RDD of millions of random numbers
    val numNumbers = 1000000 // 1 million random numbers
    val numPartitions = 4    // Initial partition count
    val randomNumbers = (1 to numNumbers).map(_ => Random.nextInt(numNumbers) + 1) // Random numbers between 1 and 100
    val largeData = sc.parallelize(randomNumbers, numPartitions)

    // Step 3: Check initial number of partitions
    println(s"Initial Partitions: ${largeData.getNumPartitions}")

    println("First 5 elements from each partition after Partitions:")
    largeData.glom().collect().foreach(arr => println(arr.take(5).mkString(", ")))
    // Step 5: Coalesce the RDD to reduce partitions (to 2)
    val coalescedData = largeData.coalesce(2)
    println(s"Coalesced Partitions: ${coalescedData.getNumPartitions}")

    // Step 6: Print the first 5 elements from each partition to observe data distribution
    println("First 5 elements from each partition after coalescing:")
    coalescedData.glom().collect().foreach(arr => println(arr.take(5).mkString(", ")))

    // Keep the application running for Spark UI inspection
    println("Press Ctrl+C to exit the application and inspect Spark UI at http://localhost:4040")
    Thread.currentThread().join()

    // Stop the Spark Context (will be reached when the application is manually terminated)
    sc.stop()
  }
}





//Exercise 1: Understanding RDD and Partitioning
//  Objective: Create and manipulate an RDD while understanding its partitions.
//  Task:
//
//  Load a large text file (or create one programmatically with millions of random numbers).
//Perform the following:
//  Check the number of partitions for the RDD.
//Repartition the RDD into 4 partitions and analyze how the data is distributed.
//  Coalesce the RDD back into 2 partitions.
//Print the first 5 elements from each partition.
//  Expected Analysis:
//
//  View the effect of repartition and coalesce in the Spark UI (stages, tasks).
