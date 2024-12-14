package com.casestudy3.utils

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._


//3. Advanced Aggregations
//Compute in-depth metrics to understand user behavior, movie popularity, and genre trends.
object AdvancedAggregations {

  //Calculate average rating, total number of ratings, and rating variance for each movie.
  def calculateAvgHighRating(dataFrame: DataFrame, cleanedMoviesDF: DataFrame): DataFrame = {
    val movieRatingsStatsDF = dataFrame
      .groupBy("movieId")
      .agg(
        avg("rating").as("average_rating"),
        count("rating").as("total_ratings"),
        variance("rating").as("rating_variance")
      )
    val enrichedMovieStatsDF = movieRatingsStatsDF
      .join(cleanedMoviesDF, Seq("movieId"), "inner")

    enrichedMovieStatsDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/avg_rating")
    enrichedMovieStatsDF

  }

  //- Identify the top 10 movies with the highest average rating (minimum 50 ratings).
  def top10MoviesWithHighAvgRating(dataFrame: DataFrame, cleanedMoviesDF: DataFrame): DataFrame = {
    val movieStatsDF = dataFrame
      .groupBy("movieId")
      .agg(
        avg("rating").as("average_rating"),
        count("rating").as("total_ratings")
      )
      .filter(col("total_ratings") >= 50)


    val enrichedMovieStatsDF = movieStatsDF
      .join(cleanedMoviesDF, Seq("movieId"), "inner")
      .select("movieId", "title", "average_rating", "total_ratings")

    val top10MoviesDF = enrichedMovieStatsDF
      .orderBy(col("average_rating").desc)
      .limit(10)
    top10MoviesDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/top10MoviesDF")
    top10MoviesDF
  }

  //- Aggregate data by genre to compute total ratings, average ratings, and most popular
  //genre.
  def aggregateByGenre(dataFrame: DataFrame): DataFrame = {
    val genreStatsDF = dataFrame
      .groupBy("genres")
      .agg(
        count("rating").as("total_ratings"),
        avg("rating").as("average_rating"),
        count("userId").as("total_users")
      )
    val mostPopularGenreDF = genreStatsDF
      .orderBy(col("total_ratings").desc)
      .limit(1)
    mostPopularGenreDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/mostPopularGenreDF")
    mostPopularGenreDF
  }
  // Calculate average rating per year using the parsed timestamp column.
  def calculateAvgRatingPerYear(dataFrame: DataFrame): DataFrame = {
    val avgRatingPerYearDF = dataFrame
      .withColumn("year", col("ratingsDF_timestamp").substr(0, 4))
      .groupBy("year")
      .agg(avg("rating").as("average_rating"))
    avgRatingPerYearDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/avgRatingPerYearDF")
    avgRatingPerYearDF
  }

  //
  def calculateActiveUser(dataFrame : DataFrame):DataFrame ={
    val activeUsersDF = dataFrame
      .groupBy("userId") // Group by userId
      .agg(
        count("rating").as("num_ratings") // Count the number of ratings per user
      )
      .orderBy(desc("num_ratings"))
    activeUsersDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/activeUsersDF")
    activeUsersDF
  }

}
