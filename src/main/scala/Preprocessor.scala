object Preprocessor {
  def transform(data: Seq[Map[String, String]], featureColumns: Array[String], labelColumn: String): (Array[Array[Double]], Array[Int]) = {
    val features = data.map { row =>
      featureColumns.map(col => row(col).toDouble)
    }.toArray

    val labels = data.map { row =>
      row(labelColumn).toInt
    }.toArray

    (features, labels)
  }
}
