name := "EquipmentFailurePrediction"

version := "1.0"

scalaVersion := "2.13.12"

// Spark dependencies
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.0",
  "org.apache.spark" %% "spark-sql" % "3.5.0",
  "org.apache.spark" %% "spark-mllib" % "3.5.0",

)
