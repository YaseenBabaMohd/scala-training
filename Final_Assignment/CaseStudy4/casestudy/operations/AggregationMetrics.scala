package casestudy.operations


import org.apache.spark.sql.functions.{avg, col, desc, sum, weekofyear}
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.storage.StorageLevel

object AggregationMetrics {

  def storeLevelMetrics(enrichedDF: DataFrame) = {
    val totalWeeklySalesByStore = enrichedDF.groupBy("Store").agg(
      sum("Weekly_Sales").alias("Total_Weekly_Sales"),
    )
    totalWeeklySalesByStore.show(10)
  //  StorageOptimisation.saveAsJson(totalWeeklySalesByStore, "totalWeeklySalesByStore")
  val storeWiseAggregatedMetricsPath = "gs://casestudy_datasets/result/totalSalesByStore"
    totalWeeklySalesByStore.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .json(storeWiseAggregatedMetricsPath)


    val avgWeeklySalesByStore = enrichedDF.groupBy("Store")
      .agg(avg("Weekly_Sales").alias("Average_Weekly_Sales"))
    avgWeeklySalesByStore.show(10)
   // StorageOptimisation.saveAsJson(avgWeeklySalesByStore, "avgWeeklySalesByStore")
   val avgWeeklySalesByStorePath = "gs://casestudy_datasets/result/avgWeeklySalesByStore"
    avgWeeklySalesByStore.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .json(avgWeeklySalesByStorePath)
    val storeMetrics = totalWeeklySalesByStore
      .join(avgWeeklySalesByStore, Seq("Store"))
      .orderBy(desc("Total_Weekly_Sales"))
      .cache()
    println("Store Level Metrics")
    println(storeMetrics.count())
  //  StorageOptimisation.saveAsJson(storeMetrics, "storeMetrics")

    val cacheMetrics = storeMetrics.persist(StorageLevel.MEMORY_ONLY)
    println("Store-Level Metrics:")
    cacheMetrics.show(10)

  }

  def DepartmentLevelMetrics(enrichedDF: DataFrame) = {

    val totalSalesByDept = enrichedDF.groupBy("Store", "Dept")
      .agg(sum("Weekly_Sales").as("Total_Sales"))
      .cache()
    totalSalesByDept.show()
   // StorageOptimisation.saveAsJson(totalSalesByDept, "totalSalesByDept")
   val avgWeeklySalesByStorePath = "gs://casestudy_datasets/result/totalSalesByDept"
    totalSalesByDept.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .json(avgWeeklySalesByStorePath)

    val weeklyTrendsByDept = enrichedDF
      .withColumn("Week", weekofyear(col("Date")))
      .groupBy("Dept", "Week")
      .agg(sum("Weekly_Sales").as("Weekly_Sales"))
      .cache()
    weeklyTrendsByDept.show()
    //StorageOptimisation.saveAsJson(weeklyTrendsByDept, "weeklyTrendsByDept")
    val weeklyTrendsByDeptPath = "gs://casestudy_datasets/result/weeklyTrendsByDept"
    weeklyTrendsByDept.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .json(weeklyTrendsByDeptPath)

    enrichedDF.show()


    val holidaySales = enrichedDF.filter("IsHoliday = true")
      .groupBy("Store", "Dept")
      .agg(sum("Weekly_Sales").alias("Holiday_Sales")).cache()

    val nonHolidaySales = enrichedDF.filter("IsHoliday = false")
      .groupBy("Store", "Dept")
      .agg(sum("Weekly_Sales").alias("NonHoliday_Sales")).cache()

    println("Holiday vs. Non-Holiday Sales:")
    val holidayAndNonHoliday = holidaySales
      .join(nonHolidaySales, Seq("Store", "Dept"), "outer")
      .orderBy(desc("Holiday_Sales"))
   // StorageOptimisation.saveAsJson(holidayAndNonHoliday, "holidayAndNonHoliday")
   val holidayAndNonHolidayPath = "gs://casestudy_datasets/result/holidayAndNonHolidayPath"
    holidayAndNonHoliday.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .json(holidayAndNonHolidayPath)
    holidayAndNonHoliday.show()

  }

}
