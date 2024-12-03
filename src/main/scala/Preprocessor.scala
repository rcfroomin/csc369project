import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.sql.DataFrame

object Preprocessor {
  def transform(data: DataFrame, featureColumns: Array[String]): DataFrame = {
    val assembler = new VectorAssembler()
      .setInputCols(featureColumns)
      .setOutputCol("features")

    val indexer = new StringIndexer()
      .setInputCol("RUL")
      .setOutputCol("indexedLabel")

    val assembledData = assembler.transform(data)
    indexer.fit(assembledData).transform(assembledData).select("features", "indexedLabel")
  }
}
