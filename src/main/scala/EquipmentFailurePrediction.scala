import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Equipment Failure Prediction")
      .master("local[*]") // Use local mode with all cores
      .getOrCreate()

    val filePath = "train_data.csv"

    val data = spark.read.option("header", "true").csv(filePath)

    val discretizedData = data.withColumn("RUL_Class", when(col("RUL") <= 50, "Low")
      .when(col("RUL") > 50 && col("RUL") <= 100, "Medium")
      .otherwise("High"))

    discretizedData.show()

    spark.stop()
  }
}