package casestudy.operations

import casestudy.operations.StorageOptimisation.filePath
import org.apache.spark.sql.{DataFrame, SaveMode}

object StorageOptimisation {

  val filePath = "gs://casestudy_datasets/result"
  def saveAsParquet(df: DataFrame, fileName: String): Unit = {
    val fileLocation = s"$filePath/$fileName"
    df.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .parquet(fileLocation)
    println(s"Data has been saved in Parquet format at: $fileLocation")
  }

  def saveAsJson(df: DataFrame, fileName: String): Unit = {
    val fileLocation = s"$filePath/$fileName"
    df.coalesce(1)
      .write
      .mode(SaveMode.Overwrite)
      .json(filePath)
    println(s"Data has been saved in JSON format at: $fileLocation")
  }

}
