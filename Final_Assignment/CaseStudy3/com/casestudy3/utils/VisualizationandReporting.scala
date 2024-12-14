package com.casestudy3.utils


import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import plotly.element.ScatterMode

import java.io.File
import plotly.layout.{Axis, BarMode, Layout}
import plotly.{Bar, Plotly, Scatter}
import shapeless.syntax.std.tuple.unitTupleOps



//8. Visualization and Reporting
//  Generate reports and visualizations based on the aggregated data.
//• - Create summary reports for genre trends, user activity, and top-rated movies.
//• - Generate plots for average ratings per year and rolling averages over time.
object VisualizationandReporting {

  //- Create summary reports for genre trends, user activity, and top-rated movies.
  def createTheReport(finalDataFrame: DataFrame) = {

    println(" Summary Reports for the Genres Trends")
    val genreTrends = finalDataFrame
      .groupBy("genres")
      .agg(
        avg("rating").alias("avg_rating"),
        countDistinct("userId").alias("unique_users"),
        functions.count("rating").alias("total_ratings")
      )
      .orderBy(desc("total_ratings"))

    genreTrends.show()
    genreTrends.coalesce(1).write.mode("overwrite").json("src/main/resources/json/visualizationandReporting")


    val userActivity = finalDataFrame
      .groupBy(col("userId")) // Group data by userId
      .agg(
        count(col("movieId")).alias("movies_rated"),
        avg(col("rating")).alias("avg_rating"),
        stddev(col("rating")).alias("rating_stddev")
      )
      .orderBy(desc("movies_rated"))

    println(" Summary Reports for the User Activity")
    userActivity.show()
    userActivity.coalesce(1).write.mode("overwrite").json("src/main/resources/json/userActivity")

    val topRatedMovies = finalDataFrame
      .groupBy("movieId")
      .agg(
        avg("rating").alias("avg_rating"),
        count("rating").alias("num_ratings")
      )
      .filter(col("num_ratings") > 100)
      .orderBy(desc("avg_rating"))

    println(" Summary Reports for the Top Rated Movies")
    topRatedMovies.show()
    topRatedMovies.coalesce(1).write.mode("overwrite").json("src/main/resources/json/topRatedMovies")


  }

  def visualiseTheData(finalDataFrame: DataFrame, sparkSession: SparkSession): Unit = {

    // Calculate average ratings per year
    calculateAverage(finalDataFrame, sparkSession)
    calulateRollingAverage(finalDataFrame, sparkSession)


  }


  def calculateAverage(finalDataFrame: DataFrame, sparkSession: SparkSession) = {
    val ratingsWithYear = finalDataFrame.withColumn("year", year(col("ratingsDF_timestamp")))
    val avgRatingsPerYear = ratingsWithYear
      .groupBy("year")
      .agg(avg("rating").alias("avg_rating"))
      .orderBy("year")

    import sparkSession.implicits._
    val years = avgRatingsPerYear.toDF().select("year").as[Int].collect()
    val avgRatings = avgRatingsPerYear.toDF().select("avg_rating").as[Double].collect()
    val yearlyPlot = Bar(
      x = years.map(_.toString).toSeq, // Ensure proper Seq type
      y = avgRatings.toSeq,            // Ensure proper Seq type
      name = "Average Ratings"
    )
    val layout = Layout(
      title = "Ratings Analysis",
      xaxis = Axis(title = "Time"),
      yaxis = Axis(title = "Average Ratings"),
      barmode = BarMode.Group
    )
    Plotly.plot("ratings-analysis.html", Seq(yearlyPlot), layout)

    println("Plots saved to ratings-analysis.html")
    // Show the results
    val outputDir = "src/main/resources/csv/avgRatingsPerYear"
    val customFileName = "avgRatingsPerYear.csv"

    // Save the DataFrame as CSV (default names)
    avgRatingsPerYear.coalesce(1).write.mode("overwrite").csv(outputDir)

    // Rename the part-00000 file to the custom file name
    val dir = new File(outputDir)
    val file = dir.listFiles().find(_.getName.startsWith("part-")).get // Find the part- file
    val renamedFile = new File(s"$outputDir/$customFileName")

    file.renameTo(renamedFile)

  }

  def calulateRollingAverage(finalDataFrame: DataFrame, sparkSession: SparkSession): Unit = {
    import org.apache.spark.sql.functions._
    import org.apache.spark.sql.expressions.Window
    import sparkSession.implicits._

    // Define the window specification for rolling averages
    val windowSpec = Window.orderBy("ratingsDF_timestamp").rowsBetween(-365, 0) // Adjust as needed

    // Add rolling average column
    val rollingAvgRatings = finalDataFrame
      .withColumn("rolling_avg", avg("rating").over(windowSpec))

    rollingAvgRatings.show()

    // Extract year from timestamp
    val plotDataFrame = rollingAvgRatings
      .withColumn("year", year($"ratingsDF_timestamp")) // Extract the year
      .groupBy("year")
      .agg(avg("rolling_avg").alias("avg_rating")) // Calculate average ratings per year
      .orderBy("year")

    plotDataFrame.show()

    // Collect data for visualization
    val years = plotDataFrame.select("year").as[Int].collect()
    val avgRatings = plotDataFrame.select("avg_rating").as[Double].collect()

    // Create a bar chart for yearly average ratings
    val yearlyPlot = Bar(
      x = years.map(_.toString).toSeq, // Convert years to strings
      y = avgRatings.toSeq,            // Convert ratings to sequence
      name = "Rolling Average Ratings"
    )

    // Layout for the plot
    val layout = Layout(
      title = "Yearly Average Ratings",
      xaxis = Axis(title = "Year"),
      yaxis = Axis(title = "Rolling Average Ratings"),
      barmode = BarMode.Group
    )

    // Save the plot as an HTML file
    Plotly.plot("ratings-analysis.html", Seq(yearlyPlot), layout)

    println("Plots saved to ratings-analysis.html")

    // Export the rolling average data to a CSV file
    rollingAvgRatings
      .select("ratingsDF_timestamp", "rolling_avg")
      .coalesce(1)
      .write
      .mode("Overwrite")
      .csv("src/main/resources/csv/RollingAverage")
  }

}


