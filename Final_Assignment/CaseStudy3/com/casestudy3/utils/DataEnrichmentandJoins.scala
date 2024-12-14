package com.casestudy3.utils

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col

//• - Perform an inner join between ratings.csv and movies.csv on movieId.
//• - Left join the result with tags.csv on movieId and userId.
//• - Use links.csv to associate movies with external metadata, if applicable.
object DataEnrichmentandJoins {

  def enrichData(moviesDF : DataFrame, ratingsDF : DataFrame, tagsDF: DataFrame, linksDF : DataFrame) : DataFrame = {
    val ratingsWithMoviesDF = ratingsDF
      .join(moviesDF, Seq("movieId"), "inner")

    val tagsRenamedDF = tagsDF.withColumnRenamed("timestamp", "tagsDF_timestamp")
    val enrichedRatingsDF = ratingsWithMoviesDF
      .join(tagsRenamedDF, Seq("movieId", "userId"))
    val finalEnrichedDF = enrichedRatingsDF
      .join(linksDF, Seq("movieId"), "inner")

    val finalCleanedDF = finalEnrichedDF.select(
      col("userId"),
      col("movieId"),
      col("rating"),
      col("timestamp").as("ratingsDF_timestamp"),
      col("title"),
      col("genres"),
      col("tag"),
      col("tagsDF_timestamp"),
      col("imdbId"),
      col("tmdbId")
    )

    finalCleanedDF

  }
}
