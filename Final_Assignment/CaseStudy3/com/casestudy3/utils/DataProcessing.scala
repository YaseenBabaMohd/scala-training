package com.casestudy3.utils

import org.apache.spark.sql.functions.{col, count, explode, when}
import org.apache.spark.sql.{DataFrame, SparkSession, functions}


//1. Data Loading and Preprocessing
//Load the MovieLens dataset into Spark DataFrames and perform essential preprocessing.
//• - Parse the timestamp columns into human-readable datetime formats.
//• - Handle missing or invalid values in all datasets.
//• - Normalize genres in movies.csv to create a one-movie-per-genre structure.
object DataProcessing {

  //Load the MovieLens dataset into Spark DataFrames and perform essential preprocessing.
  def loadData(spark: SparkSession, path: String): DataFrame = {
    spark.read.option("header", "true").csv(path)
  }

  // - Handle missing or invalid values in all datasets.
  def cleanedRatingsDF(spark: SparkSession, ratingsDF: DataFrame) : DataFrame = {
    val cleanedRatingsDF = ratingsDF.na.drop(Seq("userId", "movieId", "rating"))
    val validatedRatingsDF = cleanedRatingsDF.filter(col("rating").between(0, 5))
    validatedRatingsDF

  }

  // - Handle missing or invalid values in all datasets.
  def cleanedMoviesDF(spark: SparkSession, moviesDF: DataFrame) : DataFrame = {
    // Drop rows with null values in critical columns
    val cleanedMoviesDF = moviesDF.na.drop(Seq("movieId", "title", "genres"))
    val validatedMoviesDF = cleanedMoviesDF.filter(col("movieId").isNotNull)
    val validatedMoviesDF2 = validatedMoviesDF.filter(col("title").isNotNull)
    val validatedMoviesDF3 = validatedMoviesDF2.filter(col("genres").isNotNull)
    val moviesWithDefaultGenresDF = validatedMoviesDF.na.fill(Map("genres" -> "Unknown"))
    moviesWithDefaultGenresDF
  }

  // - Handle missing or invalid values in all datasets.
  def cleanedTagsDF(spark: SparkSession, tagsDF: DataFrame) : DataFrame = {
    val cleanedTagsDF = tagsDF.na.drop(Seq("userId", "movieId", "tag" ,"timestamp"))
    val validatedTagsDF = cleanedTagsDF.filter(col("tag").isNotNull)
    val tagsWithDefaultTagDF = validatedTagsDF.na.fill(Map("tag" -> "No Tag"))
    validatedTagsDF
  }

  // - Handle missing or invalid values in all datasets.
  def countNulls(df: org.apache.spark.sql.DataFrame): Unit = {
    df.columns.foreach { colName =>
      df.select(colName).agg(count(when(col(colName).isNull, 1)).alias("null_count")).show()
    }
  }

  //- Normalize genres in movies.csv to create a one-movie-per-genre structure.
  def normalizeGenres(moviesDF: DataFrame): DataFrame = {
    moviesDF.withColumn("genres", explode(functions.split(col("genres"), "\\|")))
  }
}
