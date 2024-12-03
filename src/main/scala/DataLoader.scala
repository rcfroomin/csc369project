import org.apache.spark.sql.{DataFrame, SparkSession}

object DataLoader {
  def loadData(spark: SparkSession, path: String): DataFrame = {
    spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(path)
  }
}

