import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._


object Main {


  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Equipment Failure Prediction")
      .master("local[*]") // Use local mode with all cores
      .getOrCreate()

    var df = spark.read
      .option("header", "true")
      .csv("train_data.csv")

    prob_with_laplace(df)

    //runStats(df)
    
  

    spark.stop()
  }

  //given the train_data.csv dataframe, return the min, max, and mean of every column except unit_number and time_cycle
   def runStats(df: DataFrame): Unit = {
    //drop the first two columns
    val filteredColumns = df.drop("unit_number", "time_cycles")
    val columnNames = filteredColumns.columns // Renaming the variable to avoid conflict

    // Create a new DataFrame by applying the transformation
    val transformedDF = columnNames.foldLeft(filteredColumns) { (tempDF, colName) =>
      tempDF.withColumn(colName, col(colName).cast("Double"))
    }

    //transformedDF.printSchema()

    // Prepare column statistics expressions
    val statsExprs = columnNames.flatMap(colName => Seq(
      min(col(colName)).alias(s"${colName}_min"),
      max(col(colName)).alias(s"${colName}_max"),
      mean(col(colName)).alias(s"${colName}_mean")
    ))

    val statsDF = transformedDF.select(statsExprs: _*)
    //statsDF.coalesce(1).write.option("header", "true").csv("statistics.csv")
    statsDF.show()
  }

  def prob_with_laplace(df : DataFrame) : Unit = {
    val size = df.columns.size
    val filteredColumns = df.drop("s_1", "s_5", "s_10", "s_16", "s_18", "s_19")
    filteredColumns.show()


    //find probablility given RUL 


  }

  //s_1 drop
  //s_2 >642 ;642 - 644 ; <644
  //s_3 >1570; 1570-1600 ; < 1600
  //s_4 >1390; 1390-1420; < 1420
  //s_5 drop
  //s_6 21.54; 21.60
  //s_7 555; 565
  //s_8 2387.25 - 2388.50
  //s_9 9050 - 9175
  //s_10 drop
  //s_11 47.25 ; 48
  //s_12 520 - 532.5
  //s_13 2387.5 - 2388.5
  //s_14 8125 -8200
  //s_15 8.2 - 8.5
  //s_16 drop
  //s_17 390 - 394
  //s_18 drop
  //s_19 drop
  //s_20 38.75 - 39.25 
  //s_21 23.2 - 23.6



}

