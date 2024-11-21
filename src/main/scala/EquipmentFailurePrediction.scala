import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Equipment Failure Prediction")
      .master("local[*]") // Use local mode with all cores
      .getOrCreate()

    println("Spark Session Initialized!")
    spark.stop()
  }
}
