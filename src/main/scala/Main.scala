import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Predictive Maintenance")
      .config("spark.driver.bindAddress", "127.0.0.1")
      .getOrCreate()

    val trainData = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("train_data.csv")

    val featureColumns = Array(
      "setting_1", "setting_2", "setting_3",
      "s_1", "s_2", "s_3", "s_4", "s_5", "s_6", "s_7", "s_8", "s_9", "s_10",
      "s_11", "s_12", "s_13", "s_14", "s_15", "s_16", "s_17", "s_18", "s_19", "s_20", "s_21"
    )

    val trainDataProcessed = Preprocessor.transform(trainData, featureColumns)

    println("Processed Training Data:")
    trainDataProcessed.show(5)

    spark.stop()
  }
}
