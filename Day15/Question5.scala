import org.apache.spark.{SparkConf, SparkContext}

object Question5 {
  def main(args: Array[String]): Unit = {
    // Step 1: Spark Configuration and Context Creation
    val sparkConf = new SparkConf()
      .setAppName("PartitioningImpactOnPerformance")
      .setMaster("local[*]") // Run locally using all available cores
      .set("spark.executor.memory", "2g") // Allocate more memory for performance

    val sc = new SparkContext(sparkConf)

    // Step 2: Load the CSV data into an RDD
    val filePath = "src/main/resources/large_dataset.csv" // Replace with your CSV file path
    val rawRDD = sc.textFile(filePath)

    // Skip the header row
    val header = rawRDD.first()

    // Remove the header row and process the rest of the data
    val dataRDD = rawRDD
      .filter(row => row != header) // Filter out the header row
      .map(line => {
        val cols = line.split(",")
        (cols(0).toInt, cols(1), cols(2).toInt, cols(3).toInt) // (id, name, age, salary)
      })


    // Function to measure the performance of partitioning and sorting
    def runExperiment(partitions: Int): Unit = {
      println(s"\nRunning with $partitions partitions...")

      // Repartition the RDD
      val partitionedRDD = dataRDD.repartition(partitions)

      // Measure the execution time for counting the rows
      val startTime = System.currentTimeMillis()
      val count = partitionedRDD.count()
      val endTime = System.currentTimeMillis()
      println(s"Row count: $count")
      println(s"Time taken to count rows with $partitions partitions: ${endTime - startTime} ms")

      // Measure the execution time for sorting
      val startSortTime = System.currentTimeMillis()
      val sortedRDD = partitionedRDD.sortBy(_._4) // Sort by salary (4th column)
      val sortedData = sortedRDD.collect() // Action to trigger computation
      val endSortTime = System.currentTimeMillis()
      println(s"Time taken to sort data with $partitions partitions: ${endSortTime - startSortTime} ms")

      // Write the sorted data back to disk
      val outputPath = s"output/partitioned_data_$partitions"
      sortedRDD.saveAsTextFile(outputPath)
      println(s"Sorted data saved to $outputPath")
    }

    // Step 4: Run the experiment with different partition sizes
    runExperiment(2)
    runExperiment(4)
    runExperiment(8)

    // Stop the Spark Context
    sc.stop()
  }
}



//Exercise 5: Partitioning Impact on Performance
//  Objective: Understand the impact of partitioning on performance and data shuffling.
//Task:
//
//  Load a large dataset (e.g., a CSV or JSON file) into an RDD.
//Partition the RDD into 2, 4, and 8 partitions separately and perform the following tasks:
//  Count the number of rows in the RDD.
//Sort the data using a wide transformation.
//  Write the output back to disk.
//Compare execution times for different partition sizes.
//  Expected Analysis:
//
//  Observe how partition sizes affect shuffle size and task distribution in the Spark UI.
//Understand the trade-off between too many and too few partitions.
