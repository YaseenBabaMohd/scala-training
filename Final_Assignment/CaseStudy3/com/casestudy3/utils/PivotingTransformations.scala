package com.casestudy3.utils

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, functions}


//5. Pivoting and Complex Transformations
//Perform complex transformations to analyze trends and anomalies.
//• - Create a pivot table showing average ratings per genre by year.
//• - Detect anomalies in ratings for movies within each genre.
//• - Calculate rating distributions for each genre (e.g., percentage of 5-star ratings).
object PivotingTransformations {

  //- Create a pivot table showing average ratings per genre by year
  def showAverageRatingsPerGenreByYear(finalDataFrame: DataFrame): DataFrame = {
    val ratingsWithYear = finalDataFrame
      .withColumn("year", year(to_date(col("ratingsDF_timestamp"))))
    val avgRatingsPerGenreByYear = ratingsWithYear
      .withColumn("genres", functions.split(col("genres"), "\\|"))
      .selectExpr("explode(genres) as genre", "year", "rating")
      .groupBy("genre", "year")
      .agg(avg("rating").as("average_rating"))

    val pivotTable = avgRatingsPerGenreByYear
      .groupBy("genre")
      .pivot("year")
      .agg(round(avg("average_rating"), 2))

    pivotTable.coalesce(1).write.mode("overwrite").json("src/main/resources/json/pivotTable")
    pivotTable

  }


  //• - Detect anomalies in ratings for movies within each genre.
  def detectAnomalies(finalDataFrame : DataFrame) ={
    val genreStatsDF = finalDataFrame
      .groupBy("genres")
      .agg(
        avg("rating").as("mean_rating"),
        stddev("rating").as("stddev_rating")
      )

    val ratingsWithStatsDF = finalDataFrame
      .join(genreStatsDF, "genres")

    val ratingsWithZScoreDF = ratingsWithStatsDF
      .withColumn(
        "z_score",
        (col("rating") - col("mean_rating")) / col("stddev_rating"))


    val anomaliesDF = ratingsWithZScoreDF.filter(functions.abs(col("z_score")) > 3)
      .select("movieId", "title", "genres", "rating", "z_score")

    anomaliesDF.show()
    anomaliesDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/anomaliesDF")

    val windowSpec = Window.partitionBy("genres").orderBy("ratingsDF_timestamp").rowsBetween(-7, 7)

    val timeBasedAnomalies = finalDataFrame
      .withColumn("rolling_avg", avg("rating").over(windowSpec))
      .withColumn("rolling_stddev", stddev("rating").over(windowSpec))
      .withColumn("z_score", (col("rating") - col("rolling_avg")) / col("rolling_stddev"))
      .filter(functions.abs(col("z_score")) > 3)
    timeBasedAnomalies.coalesce(1).write.mode("overwrite").json("src/main/resources/json/timeBasedAnomalies")
    timeBasedAnomalies.show()


  }


  //Calculate rating distributions for each genre (e.g., percentage of 5-star ratings).
  def distributionEach(userActivityDF: DataFrame)={
    val genreRatingCountsDF = userActivityDF
      .groupBy("genres", "rating")
      .agg(count("*").as("rating_count"))

    val totalRatingsPerGenreDF = userActivityDF
      .groupBy("genres")
      .agg(count("*").as("total_ratings"))

    val ratingDistributionDF = genreRatingCountsDF
      .join(totalRatingsPerGenreDF, "genres")
      .withColumn(
        "percentage",
        (col("rating_count") / col("total_ratings")) * 100
      )
      .orderBy("genres", "rating")

    ratingDistributionDF.show()
    ratingDistributionDF.coalesce(1).write.mode("overwrite").json("src/main/resources/json/ratingDistributionDF")

  }



}
