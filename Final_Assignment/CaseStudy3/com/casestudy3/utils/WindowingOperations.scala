package com.casestudy3.utils

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

//4. Windowing Operations
//Use window functions to gain time-based insights.
//• - Rank movies by rating count and average rating within each genre.
//• - Calculate rolling average ratings for each movie over a 30-day window.
//• - Analyze user activity trends based on the number of ratings over time.

object WindowingOperations {

  //• - Rank movies by rating count and average rating within each genre.
  def rankMoviesByGenre(finalDataFrame: DataFrame) :DataFrame= {
    val movieGenreRatingsDF = finalDataFrame
      .groupBy("movieId", "title", "genres") // Group by movieId, title, and genres
      .agg(
        count("rating").as("rating_count"),  // Total number of ratings
        avg("rating").as("average_rating")  // Average rating
      )

    val genreWindow = Window.partitionBy("genres").orderBy(desc("rating_count"), desc("average_rating"))

    val rankedMoviesDF = movieGenreRatingsDF
      .withColumn("rank", rank().over(genreWindow))
      .orderBy("genres", "rank")
    rankedMoviesDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/rankedMoviesDF")
    rankedMoviesDF

  }

  //- Calculate rolling average ratings for each movie over a 30-day window.
  def Day30sWindow(ratingDF: DataFrame, movieDF: DataFrame) :DataFrame= {
    val ratingsWithUnixTimeDF = movieDF.join(ratingDF, Seq("movieId"), "inner")
      .withColumn("timestamp", unix_timestamp((col("timestamp"))))

    val rollingWindowSpec = Window
      .partitionBy("movieId")
      .orderBy("timestamp")
      .rangeBetween(-2592000, 0)

    val rollingAvgRatingsDF = ratingsWithUnixTimeDF
      .withColumn("rolling_avg_rating", avg("rating").over(rollingWindowSpec))
      .orderBy("movieId", "timestamp")
    rollingAvgRatingsDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/rollingAvgRatingsDF")
    rollingAvgRatingsDF
  }

  //- Analyze user activity trends based on the number of ratings over time.
  def userActivityTrends(finalDataFrame: DataFrame)={
    val dailyActivityDF = finalDataFrame
      .groupBy("userId","ratingsDF_timestamp")
      .agg(count("userId").as("total_ratings"))
      .orderBy("ratingsDF_timestamp")
    println("Daily Activity Trend")
    dailyActivityDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/dailyActivityDF")
    dailyActivityDF.show()

    val monthlyActivityDF = finalDataFrame
      .withColumn("month", date_format(col("ratingsDF_timestamp"), "yyyy-MM"))
      .groupBy("userId", "month")
      .agg(count("userId").as("total_ratings"))
      .orderBy("month")
    println("Monthly Activity Trend")
    monthlyActivityDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/monthlyActivityDF")
    monthlyActivityDF.show()




  }

}
