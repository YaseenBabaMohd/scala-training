import com.casestudy3.utils.DataProcessing.countNulls
import com.casestudy3.utils.{AdvancedAggregations, DataEnrichmentandJoins, DataProcessing, PivotingTransformations, UserBehaviorAnalysis, VisualizationandReporting, WindowingOperations}
object CaseStudy3 {
  def main(args: Array[String]): Unit = {
    val spark = org.apache.spark.sql.SparkSession.builder
      .master("local[*]")
      .appName("Case Study 3")
      .getOrCreate()

    val sc = spark.sparkContext
    sc.setLogLevel("ERROR")
//---------1. Data Loading and Preprocessing-------------------
    val movieData = "src/main/resources/movie.csv"
    val ratingData = "src/main/resources/rating.csv"
    val tagData = "src/main/resources/tag.csv"
    val linkData = "src/main/resources/link.csv"


    val movieDF = DataProcessing.loadData(spark, movieData)
    val ratingDF = DataProcessing.loadData(spark, ratingData)
    val tagDF = DataProcessing.loadData(spark, tagData)
    val linkDF = DataProcessing.loadData(spark, linkData)

    movieDF.show()
    ratingDF.show()
    tagDF.show()
    linkDF.show()

    val cleanedRatingsDF = DataProcessing.cleanedRatingsDF(spark, ratingDF)
    val cleanedMoviesDF = DataProcessing.cleanedMoviesDF(spark, movieDF)
    val cleanedTagsDF = DataProcessing.cleanedTagsDF(spark, tagDF)

    countNulls(cleanedTagsDF)
    countNulls(cleanedMoviesDF)
    countNulls(cleanedRatingsDF)

    val normalizedMoviesDF = DataProcessing.normalizeGenres(cleanedMoviesDF)
    normalizedMoviesDF.show()


//    --------2. Data Enrichment and Joins-------------------------

    println(" Enrichment Data")
    val finalDataFrame = DataEnrichmentandJoins.enrichData(normalizedMoviesDF, cleanedRatingsDF, cleanedTagsDF, linkDF)
    finalDataFrame.cache()
    finalDataFrame.show()



    // -----------3. Advanced Aggregations-------------------------------

    println("- Calculate average rating, total number of ratings, and rating variance for each movie.")
    val movieRatingsStatsDF = AdvancedAggregations.calculateAvgHighRating(finalDataFrame,cleanedMoviesDF);
    movieRatingsStatsDF.show()

    println("- Identify the top 10 movies with the highest average rating (minimum 50 ratings).")
    val top10MoviesDF = AdvancedAggregations.top10MoviesWithHighAvgRating(finalDataFrame,cleanedMoviesDF)
    top10MoviesDF.show()

    println("Aggregate data by genre to compute total ratings, average ratings, and most popular genre.")
    val aggregateByGenre = AdvancedAggregations.aggregateByGenre(finalDataFrame)
    aggregateByGenre.show()

    println("Calculate average rating per year using the parsed timestamp column.")
    val avgRatingPerYearDF = AdvancedAggregations.calculateAvgRatingPerYear(finalDataFrame)
    avgRatingPerYearDF.show()

    println("• - Determine the top 5 most active users based on the number of ratings.")
    val activeUsersDF = AdvancedAggregations.calculateActiveUser(finalDataFrame)
    activeUsersDF.limit(5).show()

   //----- 4. Windowing Operations  Use window functions to gain time-based insights.-----/

    println("Rank movies by rating count and average rating within each genre.")
    val rankedMoviesByGenre = WindowingOperations.rankMoviesByGenre(finalDataFrame)
    rankedMoviesByGenre.show()

    println("Calculate the average rating for each movie over the last 30 days.")
    val avgRatingPerMovieLast30Days = WindowingOperations.Day30sWindow(ratingDF, movieDF)
    avgRatingPerMovieLast30Days.show()

    println("- Analyze user activity trends based on the number of ratings over time.")
    WindowingOperations.userActivityTrends(finalDataFrame)

    //--------------5. Pivoting and Complex Transformations--------------
    //Perform complex transformations to analyze trends and anomalies.

    println(" Pivoting and Complex Transformations")
    PivotingTransformations.showAverageRatingsPerGenreByYear(finalDataFrame).show()

    println("Detect anomalies in ratings for movies within each genre.")
    PivotingTransformations.detectAnomalies(finalDataFrame)

    println(" Calculate rating distributions for each genre (e.g., percentage of 5-star ratings).")
    PivotingTransformations.distributionEach(finalDataFrame)

    //----------------6. User Behavior Analysis----------------
    //Deep dive into user preferences and rating patterns.


    println(" User Behavior Analysis")
    UserBehaviorAnalysis.userBehaviorAnalysis(finalDataFrame)

    println(" • - Identify the most tagged movies and the most common tags across all users.")
    UserBehaviorAnalysis.mostTaggedMovies(finalDataFrame)

  // Store enriched datasets in Parquet format, partitioned by genre.
    finalDataFrame.coalesce(1).write.mode("overwrite").partitionBy("genres").parquet("src/main/resources/parquet/data")

      //8. Visualization and Reporting
      //Generate reports and visualizations based on the aggregated data.

      //• - Create summary reports for genre trends, user activity, and top-rated movies.
      VisualizationandReporting.createTheReport(finalDataFrame)
      VisualizationandReporting.visualiseTheData(finalDataFrame,spark)

      spark.stop()


  }
}
