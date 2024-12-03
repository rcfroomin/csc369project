import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

import org.apache.spark.SparkContext._
import scala.io._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.rdd._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import scala.collection._



object Main {
  val spark = SparkSession.builder()
      .appName("Equipment Failure Prediction")
      .master("local[*]") // Use local mode with all cores
      .getOrCreate()
  val sc = spark.sparkContext

  var sensor_range = (List (("s_2", (642.0, 643.5)),
                          ("s_3", (1580.0, 1600.0)),
                          ("s_4", (1390.0, 1420.0)),
                          ("s_6", (21.54, 21.60)),
                          ("s_7", (555.0, 565.0)),
                          ("s_8", (2387.25, 2388.5)),
                          ("s_9", (9050.0, 9170.0)),
                          ("s_11", (47.25, 48.0)),
                          ("s_12", (520.0, 532.5)),
                          ("s_13", (2387.5, 2388.5)),
                          ("s_14", (8125.0, 8200.0)),
                          ("s_15", (8.3, 8.5)),
                          ("s_17", (390.0, 394.0)),
                          ("s_20", (38.75, 39.25)),
                          ("s_21", (23.2, 23.6)), 
                          ("RUL", (68.0, 137.0))))

  def main(args: Array[String]): Unit = {
    

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    var df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("train_data.csv")

    //runStats(df)
    prob_with_laplace(data_classification(df))
    //
    

    
  

    
    
  

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

def data_classification(df : DataFrame): DataFrame = {
    val rangeMap = sensor_range.toMap

    // UDF to classify values based on ranges
    val classifyValue = udf((sensor: String, value: Any) => {
      rangeMap.get(sensor) match {
        case Some((lower, upper)) =>
          // Cast value, lower, and upper to Double explicitly before comparison
          val valDouble = value match {
              case v: Int => v.toDouble   // Convert Int to Double
              case v: Double => v         // If it's already a Double, use it as is
              case _ => 0.0               // Handle other types (such as String or null), default to 0.0
            }
          val lowerDouble = lower.toDouble
          val upperDouble = upper.toDouble
          
          if (valDouble < lowerDouble) "low"
          else if (valDouble >= lowerDouble && valDouble <= upperDouble) "mid"
          else "high"
        case None => "unknown" // If sensor is not found in the range map
      }
    })

    
    // Apply the UDF to each sensor column
    val classifiedDf = df.columns.foldLeft(df) { (tempDf, colName) =>
      if (rangeMap.contains(colName)) {
        tempDf.withColumn(s"${colName}_classification", classifyValue(lit(colName), col(colName)))
      } else {
        tempDf
      }
    }

    val classificationColumns = classifiedDf.columns.filter(_.endsWith("_classification")) //gets the column names
    val resultDf = classifiedDf.select(classificationColumns.map(col): _*) //selects only those column names 
    resultDf.show()
    return resultDf

  }

  def prob_with_laplace(df : DataFrame) : Unit = {
    val size = df.columns.size
    val classificationColumns = df.columns.filter(_.startsWith("s_"))

    val colCounts = classificationColumns.map { colName =>
      println(colName)
      val counts = df.groupBy(colName).count().collect()
      val countsMap = counts.map(row => (row.getString(0), row.getLong(1))).toMap
      (colName, countsMap)}

    colCounts.foreach{line => println(line)}

    val lambda = 1 / counts
    val m_i = 3 

    // n_ij = sensor and result cnt
    // lambda = 1 / t
    // n_j = result_cnt
    // m_i = 3

    // p = (n_ij + lambda) / (n_j + lambda * m_i)


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

